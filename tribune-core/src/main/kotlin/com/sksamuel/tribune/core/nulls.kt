package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftNel
import arrow.core.right

/**
 * Maps a [Parser] that produces a nullable output, to one that produces a non-nullable
 * output, by replacing any nulls with the result of the given function [ifNull].
 *
 * @param ifNull the default generating function
 *
 * @return the same underlying value if not null, or the default otherwise
 */
fun <I, A, E> Parser<I, A?, E>.withDefault(ifNull: () -> A): Parser<I, A, E> =
   map {
      it ?: ifNull()
   }

/**
 * Widens an existing non-nullable [Parser] I => A to accept null inputs, I? => A?
 */
fun <I, A, E> Parser<I, A, E>.allowNulls(): Parser<I?, A?, E> =
   Parser { input -> if (input == null) Either.Right(null) else this@allowNulls.parse(input) }

/**
 * Composes an existing nullable producing [Parser] to return an error if the output is null, or
 * a success otherwise.
 *
 * @param ifNull the error generating function
 *
 * @return valid if the output string is not null, otherwise invalid.
 */
fun <I, A, E> Parser<I, A?, E>.notNull(ifNull: () -> E): Parser<I, A, E> =
   Parser { input: I -> this@notNull.parse(input).flatMap { it?.right() ?: ifNull().leftNel() } }

/**
 * Composes this [Parser] to never fail, by replacing any failing values with null.
 */
fun <I, A, E> Parser<I, A, E>.failAsNull(): Parser<I, A?, Nothing> =
   Parser { input: I -> this@failAsNull.parse(input).fold({ Either.Right(null) }, { it.right() }) }
