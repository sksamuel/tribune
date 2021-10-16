package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.Validated
import com.sksamuel.monkeytail.core.validation.flatMap
import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid

/**
 * A [Parser] accepts input <I> and returns a Validated<E,A> based on validation rules.
 */
fun interface Parser<in I, out A, out E> {

   companion object {

      /**
       * Returns an identity [Parser] for a type I.
       *
       * This can be used as the entry point to building a parser. Eg,
       *
       * Parser<T>()...parse(t)
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.valid() }

      val string: Parser<String, String, Nothing> = Parser { it.valid() }
   }

   fun parse(input: I): Validated<E, A>
}

fun <I> I.parser(): Parser<I, I, Nothing> = Parser { it.valid() }

/**
 * Returns a [Parser] backed by the given function.
 */
fun <I, A, E> parser(fn: (I) -> Validated<E, A>) = Parser<I, A, E> { input -> fn(input) }

/**
 * Returns a [Parser] that maps the result of this parser by invoking the given function [f]
 * when the underlying parser returns a valid instance.
 *
 * This function performs no validation. It simply modifies the output from the underlying parser.
 *
 * @param f the function invoked to map the output of the underlying parser.
 *
 * @return a parser which returns the modified result of this parser.
 */
fun <I, A, B, E> Parser<I, A, E>.map(f: (A) -> B): Parser<I, B, E> = parser { this@map.parse(it).map(f) }

/**
 * Returns a [Parser] that maps the non-null results of a nullable parser.
 *
 * If the output of the underlying parser is null, then it is passed through unchained.
 * Otherwise, the result A is transformed by the function [f] to B.
 *
 * @param f the function invoked to map the non-null output of the underlying parser.
 *
 * @return a parser which returns the modified result of this parser.
 */
fun <I, A, B, E> Parser<I, A?, E>.mapIfNotNull(f: (A) -> B): Parser<I, B?, E> = parser { input ->
   this@mapIfNotNull.parse(input).map { if (it == null) null else f(it) }
}

/**
 * Returns a [Parser] that maps the result of this parser by invoking the given function [f]
 * and flattening the output of that function.
 *
 * @param f the function invoked to map the output of the underlying parser.
 *
 * @return a parser which returns the modified and flattened result of this parser.
 */
fun <I, A, B, E> Parser<I, A, E>.flatMap(f: (A) -> Validated<E, B>): Parser<I, B, E> =
   parser { this@flatMap.parse(it).flatMap(f) }

/**
 * Returns a [Parser] that rejects the output of this parser if the output fails to pass
 * the given predicate [p].
 *
 * In other words, if the underlying parser returns a valid output, that output is then
 * passed to the given function, and if that function returns false, it is rejected.
 * The error message is provided by the given [ifFalse] function.
 *
 * @param p the predicate to test input
 * @param ifFalse the error generating function
 *
 * @return a parser which rejects input based on the result of predicate [p]
 */
fun <I, A, E> Parser<I, A, E>.filter(p: (A) -> Boolean, ifFalse: (A) -> E): Parser<I, A, E> {
   return flatMap { if (p(it)) it.valid() else ifFalse(it).invalid() }
}
