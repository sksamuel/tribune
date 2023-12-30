package com.sksamuel.tribune.core.collections

import arrow.core.leftNel
import arrow.core.sequence
import com.sksamuel.tribune.core.Parser

/**
 * Lifts an existing [Parser] to support lists of the input types supported by
 * the underlying parser.
 *
 * In other words, given a parser from I to A, returns a parser from List<I> to List<A>.
 *
 * @return a parser that accepts lists
 */
fun <I, A, E> Parser<I, A, E>.asList(): Parser<Collection<I>, List<A>, E> {
   return Parser { input ->
      input.map { this@asList.parse(it) }.sequence()
   }
}

fun <I, A, E> Parser.Companion.list(elementParser: Parser<I, A, E>): Parser<Collection<I>, List<A>, E> =
   elementParser.asList()

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
fun <I, A, E> Parser<I, A, E>.asList(
   min: Int = 0,
   max: Int = Int.MAX_VALUE,
   ifInvalidSize: (Int) -> E
): Parser<List<I>, List<A>, E> {
   return Parser { input ->
      if ((min..max).contains(input.size)) input.map { this@asList.parse(it) }.sequence()
      else ifInvalidSize(input.size).leftNel()
   }
}
