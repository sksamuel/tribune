package com.sksamuel.tribune.examples

import com.sksamuel.tribune.examples.opaque_valueclass.ParsedBook
import com.sksamuel.tribune.spring.ResponseType
import com.sksamuel.tribune.spring.jsonResponseHandler
import com.sksamuel.tribune.spring.withParsed
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
class BookRestController {

   @RequestMapping(value = ["/books"], method = [RequestMethod.POST])
   fun createUser(@RequestBody bookInput: BookInput): ResponseEntity<out ResponseType<out ParsedBook>> =
      withParsed(bookInput, bookParser, ::jsonResponseHandler) {
         // so here we'd usually do some serious stuff with services downstream...
         // as an example this is just the identity function
         it
      }
}
