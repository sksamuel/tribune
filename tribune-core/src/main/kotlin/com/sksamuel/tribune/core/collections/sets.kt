package com.sksamuel.tribune.core.collections

import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import arrow.core.toNonEmptyListOrNull
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map

/**
 * Lifts an existing [Parser] to support sets of the input types supported by
 * the underlying parser.
 *
 * In other words, given a parser from I to A, returns a parser from Collection<I> to Set<A>.
 *
 * @return a [Parser]  that produces sets
 */
fun <I, O, E> Parser<I, O, E>.asSet(): Parser<Collection<I>, Set<O>, E> {
   val self = this
   return Parser { input ->
      val results = input.map { self.parse(it) }
      val lefts: List<E> = results.mapNotNull { it.leftOrNull() }.flatten()
      val rights: List<O> = results.mapNotNull { it.getOrNull() }
      if (lefts.isNotEmpty())
         lefts.toNonEmptyListOrNull()?.left() ?: error("unknown")
      else
         rights.toSet().right()
   }
}

fun <I, O, E> Parser<I, Set<O?>, E>.filterNulls(): Parser<I, Set<O>, E> = map { it.filterNotNull().toSet() }

/**
 * Wraps a [Parser] that produces sets with nullable items, to filter any null entries in the outputted set.
 * In other words, transforms a Parser I -> Set<O?> to I -> Set<O>.
 */
fun <I, O, E> Parser.Companion.set(elementParser: Parser<I, O, E>): Parser<Collection<I>, Set<O>, E> =
   elementParser.asSet()
