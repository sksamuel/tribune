package com.sksamuel.optio.core

import arrow.core.Validated

/**
 * Returns a [Parser] that rejects the output of this parser if the output fails to pass
 * the given predicate [p].
 *
 * In other words, if the underlying parser returns a valid output, that output is then
 * passed to the given predicate, and if that predicate returns false, it is rejected.
 *
 * The error message is provided by the given [ifFalse] function applied to the input.
 *
 * @param p the predicate to test input
 * @param ifFalse the error generating function
 *
 * @return a parser which rejects input based on the result of predicate [p]
 */
fun <I, A, E> Parser<I, A, E>.filter(p: (A) -> Boolean, ifFalse: (A) -> E): Parser<I, A, E> {
   return flatMap { if (p(it)) Validated.validNel(it) else Validated.invalidNel(ifFalse(it)) }
}
