package com.sksamuel.tribune.spring

import arrow.core.Nel
import arrow.core.NonEmptyList
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.sksamuel.tribune.core.Parser
import org.springframework.http.ResponseEntity

sealed class ResponseType<A> {
   data class ErrorResponse(val errors: String): ResponseType<Nothing>()
   data class SuccessResponse<A>(@field:JsonUnwrapped val result: A): ResponseType<A>()
}

fun jsonResponseHandler(nel: Nel<String>): ResponseEntity<ResponseType.ErrorResponse> {
   val errors = errorsToJsonString(nel)
   return ResponseEntity.badRequest().body(ResponseType.ErrorResponse(errors))
}

private fun errorsToJsonString(nel: Nel<String>): String =
   nel.joinToString()

fun <I, A, E> withParsed(
   input: I,
   parser: Parser<I, A, E>,
   handler: (NonEmptyList<E>) -> ResponseEntity<ResponseType.ErrorResponse>,
   f: (A) -> ResponseEntity<ResponseType.SuccessResponse<A>>,
): ResponseEntity<out ResponseType<out A>> {
   return parser.parse(input).fold(
      { handler(it) },
      { f(it) }
   )
}
