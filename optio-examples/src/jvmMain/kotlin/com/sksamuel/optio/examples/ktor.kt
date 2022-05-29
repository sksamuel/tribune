package com.sksamuel.optio.examples

import com.sksamuel.optio.ktor.jsonHandler
import com.sksamuel.optio.ktor.withParsedInput
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

fun Route.endpoints() {
   put("book") {
      withParsedInput(bookParser, jsonHandler) {
         println("Saving book $it")
         call.respond(HttpStatusCode.Created, "Book created")
      }
   }
}

suspend fun main() {

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
}
