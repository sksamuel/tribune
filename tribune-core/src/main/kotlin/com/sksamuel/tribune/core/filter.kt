package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right

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
fun <I, O, E> Parser<I, O, E>.filter(p: (O) -> Boolean, ifFalse: (O) -> E): Parser<I, O, E> {
   return flatMap { if (p(it)) it.right() else ifFalse(it).leftNel() }
}

/**
 * Returns a [Parser] that produces a null if the input value fails to pass the predicate [p].
 *
 * In other words, if the underlying parser returns a valid output, that output is then
 * passed to the given predicate, and if that predicate returns false, a null is produced.
 *
 * @param p the predicate to test input
 *
 * @return a parser which rejects input based on the result of predicate [p]
 */
fun <I, O : Any, E> Parser<I, O, E>.nullIf(fn: (O) -> Boolean): Parser<I, O?, E> =
   this.map { if (fn(it)) null else it }

/**
 * Returns a [Parser] that produces a null if the input value fails to pass the predicate [p].
 *
 * In other words, if the underlying parser returns a valid output, that output is then
 * passed to the given predicate, and if that predicate returns false, a null is produced.
 *
 * This variant of [nullIf] allows the output of the preceeding parser to already be null.
 *
 * @param p the predicate to test input
 *
 * @return a parser which rejects input based on the result of predicate [p]
 */
@JvmName("nullIfNullable")
fun <I, O, E> Parser<I, O?, E>.nullIf(fn: (O) -> Boolean): Parser<I, O?, E> =
   this.map { if (it == null || fn(it)) null else it }

