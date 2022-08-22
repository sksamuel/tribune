package com.sksamuel.tribune.ktor

import arrow.core.NonEmptyList
import com.sksamuel.tribune.core.Parser
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass

/**
 * A [Handler] is responsible for returning an error response to a caller.
 *
 * It is invoked with the context of the route where the parsing failed, along with
 * a non-empty list of errors of type E.
 */
typealias Handler<E> = suspend (PipelineContext<Unit, ApplicationCall>, NonEmptyList<E>) -> Unit

/**
 * Composes two handlers together, eg handler1.compose(handler2).
 * Both handlers are invoked, with this invoked first, and other invoked second.
 */
fun <E> Handler<E>.compose(other: Handler<E>): Handler<E> = { context, errors ->
   this(context, errors)
   other(context, errors)
}

/**
 * This [Handler] simply returns an error response as a 400 Bad Request without a body.
 * This is suitable for when we don't want to return error details to the caller.
 */
val badRequestHandler: Handler<*> = { context, _ ->
   context.call.respond(HttpStatusCode.BadRequest)
}

/**
 * This [Handler] logs the errors to info level, but does not return a response to the user.
 * This handler should be composed with another handler that does return a response.
 */
val loggingHandler: Handler<*> = { context, errors ->
   context.application.log.info("Validation failed with errors: $errors")
}

/**
 * This [Handler] returns an error response as a 400 Bad Request, with the body as a concatenated
 * list of errors, with the response content type set to text/plain.
 *
 * This handler accepts any type of error, and each error is converted to a string by invoking
 * .toString() on the instance.
 */
val textPlainHandler: Handler<*> = { context, errors ->
   context.call.respondText(
      status = HttpStatusCode.BadRequest,
      text = errors.joinToString(", "),
      contentType = ContentType.Text.Plain,
   )
}

/**
 * This [Handler] returns an error response as a 400 Bad Request, with the body as a json array, with
 * each error being one element of the array.
 *
 * This handler accepts any type of error, and each error is converted to a string by invoking
 * .toString() on the instance.
 */
val jsonHandler: Handler<*> = { context, errors ->
   val newline = System.lineSeparator()
   val errorLines = errors.joinToString("\",$newline\"", "\"", "\"") { it.toString().replace("\"", "\\\"") }
   val json = """[$newline$errorLines$newline]"""
   context.call.respondText(
      status = HttpStatusCode.BadRequest,
      text = json,
      contentType = ContentType.Application.Json,
   )
}

/**
 * Extension function to be used inside a Ktor route, that uses the provided [parser] to
 * parse the body of the request.
 *
 * If the parser returns an error, then the provided [handler] will be invoked to
 * return an error response to the caller.
 *
 * Otherwise, if the parser returns a valid result, the provided function [f] is invoked with that result.
 */
suspend inline fun <reified I : Any, A, E> PipelineContext<Unit, ApplicationCall>.withParsedBody(
   parser: Parser<I, A, E>,
   noinline handler: suspend (PipelineContext<Unit, ApplicationCall>, NonEmptyList<E>) -> Unit = jsonHandler,
   noinline f: suspend (A) -> Unit,
) = withParsedBody(I::class, parser, handler, f)

suspend fun <I : Any, A, E> PipelineContext<Unit, ApplicationCall>.withParsedBody(
   type: KClass<I>,
   parser: Parser<I, A, E>,
   handler: suspend (PipelineContext<Unit, ApplicationCall>, NonEmptyList<E>) -> Unit = jsonHandler,
   f: suspend (A) -> Unit,
) {
   val input = call.receive(type)
   parser.parse(input).fold(
      { handler(this, it) },
      { f(it) }
   )
}
