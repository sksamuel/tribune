package com.sksamuel.tribune.spring

import arrow.core.Nel
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.sksamuel.tribune.core.Parser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class ResponseType<A> {
   data class ErrorResponse<A>(val errors: String): ResponseType<A>()
   data class SuccessResponse<A>(@field:JsonUnwrapped val result: A): ResponseType<A>()
}

fun <R> errorResponseHandler(nel: Nel<String>): ResponseEntity<ResponseType<R>> =
   ResponseEntity.badRequest().body(ResponseType.ErrorResponse(errorsToJsonString(nel)))

fun <A, R> successResponseHandler(service: (A) -> R, parsedResult: A, code: HttpStatus): ResponseEntity<ResponseType<R>> =
   ResponseEntity.status(code).body(ResponseType.SuccessResponse(service(parsedResult)))


private fun <E> errorsToJsonString(nel: Nel<E>): String =
   nel.toList().joinToString()

fun <I, A, E, R> withParsed(
   input: I,
   parser: Parser<I, A, E>,
   errorHandler: (Nel<E>) -> ResponseEntity<ResponseType<R>>,
   successHandler: ((A) -> R, A, HttpStatus) -> ResponseEntity<ResponseType<R>>,
   code: HttpStatus,
   service: (A) -> R
): ResponseEntity<out ResponseType<out R>> =
   parser.parse(input).fold(
      { errorHandler(it) },
      { successHandler(service, it, code) }
   )

fun <R> errorResponseHandlerSafe(nel: Nel<String>): ResponseEntity<R> =
   ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

fun <A, R> successResponseHandlerSafe(service: (A) -> R, parsedResult: A, code: HttpStatus): ResponseEntity<R> {
   // TODO at least log the errors
   return ResponseEntity.status(code).body(service(parsedResult))
}

fun <I, A, E, R> withParsedSafe(
   input: I,
   parser: Parser<I, A, E>,
   errorHandlerSafe: (Nel<E>) -> ResponseEntity<R>,
   successHandlerSafe: ((A) -> R, A, HttpStatus) -> ResponseEntity<R>,
   code: HttpStatus,
   service: (A) -> R
): ResponseEntity<out R> =
   parser.parse(input).fold(
      { errorHandlerSafe(it) },
      { successHandlerSafe(service, it, code) }
   )
