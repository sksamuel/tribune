package com.sksamuel.tribune.spring

import arrow.core.Nel
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.sksamuel.tribune.core.Parser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class ExplicitResponse<A> {
   data class ExplicitErrorResponse<A>(val errors: String): ExplicitResponse<A>()
   data class ExplicitSuccessResponse<A>(@field:JsonUnwrapped val result: A): ExplicitResponse<A>()
}

fun <R> errorResponseHandlerExplicit(nel: Nel<String>): ResponseEntity<ExplicitResponse<R>> =
   ResponseEntity.badRequest().body(ExplicitResponse.ExplicitErrorResponse(errorsToJsonString(nel)))

fun <A, R> successResponseHandlerExplicit(service: (A) -> R, parsedResult: A, code: HttpStatus): ResponseEntity<ExplicitResponse<R>> =
   ResponseEntity.status(code).body(ExplicitResponse.ExplicitSuccessResponse(service(parsedResult)))

fun <I, A, E, R> withParsedExplicit(
   input: I,
   parser: Parser<I, A, E>,
   errorHandlerExplicit: (Nel<E>) -> ResponseEntity<ExplicitResponse<R>>,
   successHandlerExplicit: ((A) -> R, A, HttpStatus) -> ResponseEntity<ExplicitResponse<R>>,
   code: HttpStatus,
   service: (A) -> R
): ResponseEntity<out ExplicitResponse<out R>> =
   parser.parse(input).fold(
      { errorHandlerExplicit(it) },
      { successHandlerExplicit(service, it, code) }
   )

fun <R> errorResponseHandlerDefault(nel: Nel<String>): ResponseEntity<R> =
   ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

fun <A, R> successResponseHandlerDefault(service: (A) -> R, parsedResult: A, code: HttpStatus): ResponseEntity<R> {
   // TODO at least log the errors
   return ResponseEntity.status(code).body(service(parsedResult))
}

fun <I, A, E, R> withParsedDefault(
   input: I,
   parser: Parser<I, A, E>,
   errorHandlerDefault: (Nel<E>) -> ResponseEntity<R>,
   successHandlerDefault: ((A) -> R, A, HttpStatus) -> ResponseEntity<R>,
   code: HttpStatus,
   service: (A) -> R
): ResponseEntity<out R> =
   parser.parse(input).fold(
      { errorHandlerDefault(it) },
      { successHandlerDefault(service, it, code) }
   )

private fun <E> errorsToJsonString(nel: Nel<E>): String =
   nel.toList().joinToString()

