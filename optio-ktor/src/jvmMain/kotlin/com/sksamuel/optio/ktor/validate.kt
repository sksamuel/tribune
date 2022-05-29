package com.sksamuel.optio.ktor

import com.sksamuel.optio.core.parsers.Parser
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend inline fun <reified I : Any, A, E> PipelineContext<Unit, ApplicationCall>.withParsedInput(
   parser: Parser<I, A, E>,
   f: (A) -> Unit
) {
   val input = call.receive<I>()
   parser.parse(input).fold(
      { call.respond(HttpStatusCode.BadRequest, it) },
      { f(it) }
   )
}
