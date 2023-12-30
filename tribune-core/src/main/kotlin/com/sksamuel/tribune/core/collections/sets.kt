package com.sksamuel.tribune.core.collections

import arrow.core.sequence
import com.sksamuel.tribune.core.Parser

/**
 * Lifts an existing [Parser] to support sets of the input types supported by
 * the underlying parser.
 *
 * In other words, given a parser from I to A, returns a parser from Set<I> to Set<A>.
 *
 * @return a [Parser]  that produces sets
 */
fun <I, A, E> Parser<I, A, E>.asSet(): Parser<Collection<I>, Set<A>, E> {
   return Parser { input ->
      input.map { this@asSet.parse(it) }.sequence().map { it.toSet() }
   }
}

fun <I, A, E> Parser.Companion.set(elementParser: Parser<I, A, E>): Parser<Collection<I>, Set<A>, E> =
   elementParser.asSet()
