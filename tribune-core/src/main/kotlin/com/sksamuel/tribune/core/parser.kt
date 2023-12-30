package com.sksamuel.tribune.core

import arrow.core.EitherNel
import arrow.core.right

/**
 * A [Parser] is a function I => [EitherNel] that parses the input I, returing either
 * an output O or an error E.
 *
 * It is implemented as an interface to allow for variance on the type parameters.
 */
fun interface Parser<in I, out O, out E> {

   companion object {

      /**
       * Returns an identity [Parser] for a type I.
       *
       * This can be used as the entry point to building a parser. Specify the source input type as
       * the input type parameter to this function, and a parser (I) -> EitherNel<Nothing, I> will
       * be returned.
       *
       * Eg:
       *
       * Parser<String>() will return an identity parser that simply returns any intput string.
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.right() }

      /**
       * Synonym for invoke.
       */
      fun <I> from() = invoke<I>()
   }

   /**
    * Parses the given [input] returning an either that contains the successfully parsed result
    * as a right, or a failure as a left.
    */
   fun parse(input: I): EitherNel<E, O>

   /**
    * Parses the given [input], returning a successful result, or null in the case of an error.
    */
   fun parseOrNull(input: I): O? = parse(input).getOrNull()

   /**
    * Returns a new Parser<J> that wraps this parser, by using the supplied function [f]
    * to convert a given [J] into an [I].
    */
   fun <J> contramap(f: (J) -> I): Parser<J, O, E> =
      Parser { parse(f(it)) }

}
