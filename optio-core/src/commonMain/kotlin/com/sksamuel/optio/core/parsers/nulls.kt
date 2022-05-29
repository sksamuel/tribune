package com.sksamuel.optio.core.parsers

import arrow.core.Validated
import arrow.core.andThen
import arrow.core.invalidNel

/**
 * Maps a [Parser] that produces a nullable output, to one that produces a non-nullable
 * output, by replacing any nulls with the result of the given function [ifNull].
 *
 * @param ifNull the default generating function
 *
 * @return the same underlying value if not null, or the default otherwise
 */
fun <I, A, E> Parser<I, A?, E>.withDefault(ifNull: () -> A): Parser<I, A, E> {
   return map { it ?: ifNull() }
}

/**
 * Composes an existing non-nullable [Parser] to accept null inputs which are returned as valid.
 */
fun <I, A, E> Parser<I, A, E>.allowNulls(): Parser<I?, A?, E> {
   return Parser { input ->
      if (input == null) Validated.Valid(null) else this@allowNulls.parse(input)
   }
}

/**
 * Composes an existing nullable producing [Parser] to return an error if the output is null, or
 * a success otherwise.
 *
 * @param ifNull the error generating function
 *
 * @return valid if the output string is not null, otherwise invalid.
 */
fun <I, A, E> Parser<I, A?, E>.notNull(ifNull: () -> E): Parser<I, A, E> {
   return Parser { input: I ->
      this@notNull.parse(input).andThen { it?.valid() ?: ifNull().invalidNel() }
   }
}

/**
 * Composes this [Parser] to never fail, by replacing any failing values with null.
 */
fun <I, A, E> Parser<I, A, E>.failAsNull(): Parser<I, A?, Nothing> {
   return Parser { input: I -> this@failAsNull.parse(input).fold({ Validated.Valid(null) }, { it.valid() }) }
}
