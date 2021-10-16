package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.traverse

/**
 * Lifts an existing [Parser] to support lists of the input types supported by
 * the underlying parser.
 *
 * In other words, given a parser from I to A, returns a parser from List<I> to List<A>.
 *
 * @return a parser that accepts lists
 */
fun <I, A, E> Parser<I, A, E>.repeated(): Parser<List<I>, List<A>, E> {
   return Parser { input ->
      input.map { this@repeated.parse(it) }.traverse()
   }
}

/**
 * Lifts an existing [Parser] to support lists of the input types supported by
 * the underlying parser. This version of repeated supports upper and lower bounds
 * on the list size.
 *
 * In other words, given a parser, this will return a parser that handles lists of the inputs.
 *
 * @param min the minimum number of elements in the list
 * @param max the maximum number of elements in the list
 *
 * @return a parser that accepts lists
 */
fun <I, A, E> Parser<I, A, E>.repeated(
   min: Int = 0,
   max: Int = Int.MAX_VALUE,
   ifInvalidSize: (Int) -> E
): Parser<List<I>, List<A>, E> {
   return Parser { input ->
      if ((min..max).contains(input.size)) input.map { this@repeated.parse(it) }.traverse()
      else ifInvalidSize(input.size).invalid()
   }
}
