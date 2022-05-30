package com.sksamuel.optio.core.collections

import arrow.core.sequence
import com.sksamuel.optio.core.Parser

fun <I, A, E> Parser.Companion.set(elementParser: Parser<I, A, E>): Parser<Collection<I>, Set<A>, E> {
   return Parser { input ->
      input.map { elementParser.parse(it) }.sequence().map { it.toSet() }
   }
}
