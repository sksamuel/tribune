package com.sksamuel.tribune.core.maps

import arrow.core.sequence
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap

fun <I, K, V, R, E> Parser<I, Map<K, V>, E>.parseKeys(parser: Parser<K, R, E>): Parser<I, Map<R, V>, E> {
   return this.flatMap { input ->
      input.map { (key, value) -> parser.parse(key).map { Pair(it, value) } }.sequence().map { it.toMap() }
   }
}

fun <I, K, V, R, E> Parser<I, Map<K, V>, E>.parseValues(parser: Parser<V, R, E>): Parser<I, Map<K, R>, E> {
   return this.flatMap { input ->
      input.map { (key, value) -> parser.parse(value).map { Pair(key, it) } }.sequence().map { it.toMap() }
   }
}
