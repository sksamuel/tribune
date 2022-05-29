package com.sksamuel.optio.examples

import com.sksamuel.optio.ktor.withParsedInput
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.put

fun Route.endpoints() {
   put("book") {
      withParsedInput(bookParser) {
         println("Saving book $it")
         call.respond(HttpStatusCode.Created, "Book created")
      }
   }
}
