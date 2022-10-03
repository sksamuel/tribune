package com.sksamuel.tribune.examples

import com.sksamuel.tribune.examples.opaque_valueclass.ParsedBook
import com.sksamuel.tribune.spring.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
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
class BookRestController {

   @RequestMapping(value = ["/books/explicit"], method = [RequestMethod.POST])
   fun createBook(@RequestBody bookInput: BookInput): ResponseEntity<out ExplicitResponse<out ParsedBook>> =
      withParsedExplicit(bookInput, bookParser, ::errorResponseHandlerExplicit, ::successResponseHandlerExplicit, HttpStatus.CREATED) {
         // so here we'd usually do some real-world stuff with services downstream
         // as an example this is just the identity function, so we are returning the parsed book
         it
      }

   @RequestMapping(value = ["/books"], method = [RequestMethod.POST])
   fun createBookSafe(@RequestBody bookInput: BookInput): ResponseEntity<out ParsedBook> =
      withParsedDefault(bookInput, bookParser, ::errorResponseHandlerDefault, ::successResponseHandlerDefault, HttpStatus.CREATED) {
         // so here we'd usually do some real-world stuff with services downstream
         // as an example this is just the identity function, so we are returning the parsed book
         it
      }
}
