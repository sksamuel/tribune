package com.sksamuel.tribune.core.collections

import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map

/**
 * Lifts an existing [Parser] to support lists of the input types supported by
 * the underlying parser.
 *
 * In other words, given a parser from I to A, returns a parser from Collection<I> to List<A>.
 *
 * @return a [Parser]  that produces sets
 */
fun <I, O, E> Parser<I, O, E>.asList(): Parser<Collection<I>, List<O>, E> {
   val self = this
   return Parser { input ->
      val results = input.map { self.parse(it) }
      val lefts: List<E> = results.mapNotNull { it.leftOrNull() }.flatten()
      val rights: List<O> = results.mapNotNull { it.getOrNull() }
      if (lefts.isNotEmpty())
         lefts.toNonEmptyListOrNull()?.left() ?: error("unknown")
      else
         rights.right()
   }
}

fun <I, O, E> Parser.Companion.list(elementParser: Parser<I, O, E>): Parser<Collection<I>, List<O>, E> =
   elementParser.asList()

/**
 * Wraps a [Parser] that produces lists with nullable items, to filter any null entries in the outputted list.
 * In other words, transforms a Parser I -> List<O?> to I -> List<O>.
 */
fun <I, O, E> Parser<I, List<O?>, E>.filterNulls(): Parser<I, List<O>, E> = map { it.filterNotNull() }
