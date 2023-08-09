package com.sksamuel.tribune.core.collections

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
fun <I, A, E> Parser<I, A, E>.asSet(): Parser<Collection<I>, Set<A>, E> {
   return Parser { input ->
      input.map { this@asSet.parse(it) }.sequence().map { it.toSet() }
   }
}

fun <I, A, E> Parser.Companion.set(elementParser: Parser<I, A, E>): Parser<Collection<I>, Set<A>, E> =
   elementParser.asSet()
