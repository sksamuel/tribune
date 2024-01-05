package com.sksamuel.tribune.core

import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.right

/**
 * A [Parser] is a function I => [EitherNel] that parses the input I, returing either
 * an output O or error E.
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

/**
 * Returns a new parser that first tries to parse the input with [this] and if it fails tries the [other].
 * If both parsers fail, errors are being accumulated
 */
fun <I, E : E2, R : R2, I2 : I, E2, R2> Parser<I, R, E>.orElse(other: Parser<I2, R2, E2>): Parser<I2, R2, E2> =
   Parser { i ->
      parse(i).fold(
         ifRight = { it.right() },
         ifLeft = { es -> other.parse(i).mapLeft { es2 -> (es as NonEmptyList<E2>) + es2 }}
      )
   }

