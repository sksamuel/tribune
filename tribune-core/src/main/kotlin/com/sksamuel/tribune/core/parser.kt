package com.sksamuel.tribune.core

import arrow.core.*

/**
 * A [Parser] is a function I => [EitherNel] that parses the input I, returning either
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
       * Parser<String>() will return an identity parser that simply returns any input string.
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
 * The two parsers have outputs [O] and [O2] that are related.
 * In particular, [O2] is supertype of [O]
 */
fun <I, E : E2, O : O2, I2 : I, E2, O2> Parser<I, O, E>.orElse(other: Parser<I2, O2, E2>): Parser<I2, O2, E2> =
   Parser { i ->
      parse(i).fold(
         ifRight = { it.right() },
         ifLeft = { es -> other.parse(i).mapLeft { es2 -> (es as NonEmptyList<E2>) + es2 } }
      )
   }

/**
 * Returns a new parser that first tries to parse the input with [this] and if it fails tries the [other].
 * The outputs of the two parsers, ([O] and [O2]) don't have to be related.
 * In case the first parser succeeds a left [O] is being returned
 * In case the second parser succeeds a right [O2] is being returned
 * If both parsers fail, errors are being accumulated
 */
fun <I, E : E2, O, I2 : I, E2, O2> Parser<I, O, E>.orElseEither(other: Parser<I2, O2, E2>): Parser<I2, Either<O, O2>, E2> =
   this.map { it.left() }.orElse(other.map { it.right() })
