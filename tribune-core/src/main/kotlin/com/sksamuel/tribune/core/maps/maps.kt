package com.sksamuel.tribune.core.maps

import arrow.core.raise.either
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.transformEither

fun <I, K, V, R, E> Parser<I, Map<K, V>, E>.parseKeys(parser: Parser<K, R, E>): Parser<I, Map<R, V>, E> {
    return this.transformEither { input ->
        either { input.map { (key, value) -> Pair(parser.parse(key).bind(), value) }.toMap() }
    }
}

fun <I, K, V, R, E> Parser<I, Map<K, V>, E>.parseValues(parser: Parser<V, R, E>): Parser<I, Map<K, R>, E> {
    return this.transformEither { input ->
        either { input.map { (key, value) -> Pair(key, parser.parse(value).bind()) }.toMap() }
    }
}
