package com.sksamuel.tribune.spring

import arrow.core.Nel
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.sksamuel.tribune.core.Parser
import org.springframework.http.ResponseEntity

sealed class ResponseType<A> {
   data class ErrorResponse<A>(val errors: String): ResponseType<A>()
   data class SuccessResponse<A>(@field:JsonUnwrapped val result: A): ResponseType<A>()
}

fun <A, R> jsonResponseHandler(nel: Nel<String>?, service: (A) -> R, parsedResult: A?): ResponseEntity<ResponseType<R>> {
   return if (nel != null) {
      val errors = errorsToJsonString(nel)
      ResponseEntity.badRequest().body(ResponseType.ErrorResponse(errors))
   } else {
      ResponseEntity.ok(ResponseType.SuccessResponse(service(parsedResult!!)))
   }
}

private fun errorsToJsonString(nel: Nel<String>): String =
   nel.toList().joinToString()

fun <I, A, E, R> withParsed(
   input: I,
   parser: Parser<I, A, E>,
   handler: (Nel<E>?, (A) -> R, A?) -> ResponseEntity<ResponseType<R>>,
   service: (A) -> R
): ResponseEntity<out ResponseType<out R>> =
   parser.parse(input).fold(
      { handler(it, service, null) },
      { handler(null, service, it) }
   )
