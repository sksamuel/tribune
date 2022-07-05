package com.sksamuel.tribune.ktor

import arrow.core.NonEmptyList
import com.sksamuel.tribune.core.Parser
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext

typealias Handler<E> = suspend (ApplicationCall, NonEmptyList<E>) -> Unit

val defaultHandler: Handler<*> = { call, errors ->
   call.respond(HttpStatusCode.BadRequest, errors.joinToString(", "))
}

val jsonHandler: Handler<String> = { call, errors ->
   val newline = System.lineSeparator()
   val errorLines = errors.joinToString("\",$newline\"", "\"", "\"") { it.replace("\"", "\\\"") }
   val json = """[$newline$errorLines$newline]"""
   call.respondText(json, ContentType.Application.Json)
   call.respond(HttpStatusCode.BadRequest, json)
}

suspend inline fun <reified I : Any, A, E> PipelineContext<Unit, ApplicationCall>.withParsedInput(
   parser: Parser<I, A, E>,
   f: (A) -> Unit,
) {
   withParsedInput(parser, defaultHandler, f)
}

suspend inline fun <reified I : Any, A, E> PipelineContext<Unit, ApplicationCall>.withParsedInput(
   parser: Parser<I, A, E>,
   handler: suspend (ApplicationCall, NonEmptyList<E>) -> Unit,
   f: (A) -> Unit,
) {
   val input = call.receive<I>()
   parser.parse(input).fold(
      { handler(call, it) },
      { f(it) }
   )
}
