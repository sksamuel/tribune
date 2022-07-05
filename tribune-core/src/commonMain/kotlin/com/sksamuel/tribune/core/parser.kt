package com.sksamuel.tribune.core

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.validNel

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
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.validNel() }

      /**
       * Synonym for invoke.
       */
      fun <I> from() = invoke<I>()
   }

   fun parse(input: I): Validated<NonEmptyList<E>, A>

   fun <J> contramap(f: (J) -> I): Parser<J, A, E> =
      Parser { this@Parser.parse(f(it)) }

}
