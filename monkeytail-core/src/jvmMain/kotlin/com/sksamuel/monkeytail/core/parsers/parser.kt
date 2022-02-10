package com.sksamuel.monkeytail.core.parsers

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.zip

/**
 * A [Parser] accepts input of type [I] and returns a [ValidatedNel].
 */
fun interface Parser<in I, out A, out E> {

   companion object {

      /**
       * Returns an identity [Parser] for a type I.
       *
       * This can be used as the entry point to building a parser. Eg,
       *
       * Parser<String>()...parse("mystring")
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.valid() }

      fun <INPUT, A, B, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
      ): Parser<INPUT, Pair<A, B>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input)) { a, b -> Pair(a, b) }
      }

      fun <INPUT, A, B, C, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
      ): Parser<INPUT, Triple<A, B, C>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input), p3.parse(input)) { a, b, c -> Triple(a, b, c) }
      }
   }

   fun parse(input: I): Validated<NonEmptyList<E>, A>

   fun <J> contramap(f: (J) -> I): Parser<J, A, E> =
      Parser { this@Parser.parse(f(it)) }

}

// helper functions for validated
fun <E, A> ValidatedNel<E, A>.getOrThrow(): A = fold({ error(it) }, { it })
fun <E, A> ValidatedNel<E, A>.getErrorsOrThrow(): NonEmptyList<E> = fold({ it }, { error(it.toString()) })
fun <A> A.valid(): ValidatedNel<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): ValidatedNel<E, Nothing> = this.invalidNel()
fun <E> List<E>.invalid(): ValidatedNel<E, Nothing> = Validated.Invalid(Nel.fromListUnsafe(this))

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
