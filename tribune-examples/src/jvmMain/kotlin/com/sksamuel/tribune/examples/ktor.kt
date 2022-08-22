package com.sksamuel.tribune.examples

import com.sksamuel.tribune.ktor.jsonHandler
import com.sksamuel.tribune.ktor.withParsedBody
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun main() {

   fun Route.endpoints() {
      put("book") {
         withParsedBody(bookParser, jsonHandler) {
            println("Saving book $it")
            call.respond(HttpStatusCode.Created, "Book created")
         }
      }
      put("bookdata") {
         withParsedBody(bookParserDataclass, jsonHandler) {
            val (bookauthor, booktitle, bookisbn) = it
            println("Saving book (data class with primitives) $bookauthor, $booktitle, $bookisbn")
            call.respond(HttpStatusCode.Created, "Book created")
         }
      }
   }

   val server = embeddedServer(Netty, port = 8080) {
      install(ContentNegotiation) { jackson() }
      routing {
         endpoints()
      }
   }.start(wait = false)

   val client = HttpClient(CIO) {
      install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) { jackson() }
   }

   client.put("http://localhost:8080/book") {
      contentType(ContentType.Application.Json)
      setBody(BookInput(null, null, null))
   }.apply { println(this.bodyAsText()) }

   client.put("http://localhost:8080/book") {
      contentType(ContentType.Application.Json)
      setBody(BookInput("my book", null, null))
   }.apply { println(this.bodyAsText()) }

   client.put("http://localhost:8080/book") {
      contentType(ContentType.Application.Json)
      setBody(BookInput(null, "some author", null))
   }.apply { println(this.bodyAsText()) }

   client.put("http://localhost:8080/book") {
      contentType(ContentType.Application.Json)
      setBody(BookInput(null, null, "9375556123"))
   }.apply { println(this.bodyAsText()) }

   client.put("http://localhost:8080/book") {
      contentType(ContentType.Application.Json)
      setBody(BookInput("my title", "some author", "9375556123"))
   }.apply { println(this.bodyAsText()) }

   client.put("http://localhost:8080/bookdata") {
      contentType(ContentType.Application.Json)
      setBody(BookInput("my title", "some author", "9375556123"))
   }.apply { println(this.bodyAsText()) }
}
