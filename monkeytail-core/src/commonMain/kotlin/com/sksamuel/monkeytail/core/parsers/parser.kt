package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.Validated
import com.sksamuel.monkeytail.core.validation.flatMap
import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid

/**
 * A [Parser] accepts input <I> and returns a Validated<E, A> based on validation rules.
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

      /**
       * An identity string parser.
       */
      val string: Parser<String, String, Nothing> = invoke<String>()
   }

   fun parse(input: I): Validated<E, A>
}

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
