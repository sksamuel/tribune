package com.sksamuel.optio.core.parsers

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel

/**
 * A [Parser] is a function I => [ValidatedNel] that parses the input I.
 *
 * It is implemented as an interface to allow for variance on the type parameters.
 */
fun interface Parser<in I, out A, out E> {

   companion object {

      /**
       * Returns an identity [Parser] for a type I.
       *
       * This can be used as the entry point to building a parser. Specify the source input type as
       * the input type parameter to this function, and a parser (I) -> ValidatedNel<Nothing, I> will
       * be returned.
       *
       * Eg:
       *
       * Parser<String>()...parse("mystring")
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.valid() }

      /**
       * Synonym for invoke.
       */
      fun <I> from() = invoke<I>()
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
