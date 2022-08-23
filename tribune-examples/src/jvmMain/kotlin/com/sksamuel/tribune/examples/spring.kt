package com.sksamuel.tribune.examples

import arrow.core.NonEmptyList
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.examples.opaque_valueclass.ParsedBook
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class BookApplication

fun main(args: Array<String>) {
   runApplication<BookApplication>(*args)
}

@RestController
@RequestMapping("/rest/book")
class BookRestController() {

   @RequestMapping(value = ["/books"], method = [RequestMethod.POST])
   fun createUser(@RequestBody bookInput: BookInput): ResponseEntity<ParsedResponse<ParsedBook>> =
      withParsed(bookInput, bookParser, jsonResponseHandler) { parsedBook ->
         // so here we'd usually do some serious stuff with services downstream...
         ResponseEntity.ok(ParsedResponse.SuccessResponse(parsedBook))
      }
}

sealed interface ParsedResponse<R> {
   data class ErrorResponse<R>(val message: String) : ParsedResponse<R>
   data class SuccessResponse<R>(val parsedResult: R) : ParsedResponse<R>
}

val jsonResponseHandler: (NonEmptyList<String>) -> ResponseEntity<ParsedResponse<ParsedBook>> = { nel ->
   val newline = System.lineSeparator()
   val errorLines = nel.joinToString("\",$newline\"", "\"", "\"") { nel.toString().replace("\"", "\\\"") }
   val json = """[$newline$errorLines$newline]"""
   ResponseEntity.badRequest().body(ParsedResponse.ErrorResponse(json))
}

fun <I, A, E> withParsed(
   input: I,
   parser: Parser<I, A, E>,
   handler: (NonEmptyList<E>) -> ResponseEntity<ParsedResponse<A>>,
   f: (A) -> ResponseEntity<ParsedResponse<A>>,
): ResponseEntity<ParsedResponse<A>> {
   return parser.parse(input).fold(
      { handler(it) },
      { f(it) }
   )
}
