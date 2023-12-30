package com.sksamuel.tribune.core.booleans

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap
import com.sksamuel.tribune.core.map

/**
 * Transforms a String producing [Parser] into a Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBoolean].
 */
fun <I, E> Parser<I, String, E>.toBoolean(): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBoolean()
      b.right()
   }

/**
 * Transforms a String producing [Parser] into a Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBooleanStrict].
 */
fun <I, E> Parser<I, String, E>.toBooleanStrict(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap { input ->
      runCatching { input.toBooleanStrict() }.fold({ it.right() }, { ifError(input).leftNel() })
   }

/**
 * Transforms a nullable String producing [Parser] into a nullable Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBooleanStrictOrNull].
 */
fun <I, E> Parser<I, String, E>.toBooleanStrictOrNull(): Parser<I, Boolean?, E> =
   map { it.toBooleanStrictOrNull() }
