package com.sksamuel.tribune.core.booleans

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.transformEither

/**
 * Transforms a String producing [Parser] into a Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBoolean].
 */
fun <I, E> Parser<I, String, E>.boolean(): Parser<I, Boolean, E> =
   transformEither {
      val b = it.toBoolean()
      b.right()
   }

/**
 * Transforms a String producing [Parser] into a Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBooleanStrict].
 */
fun <I, E> Parser<I, String, E>.booleanStrict(ifError: (String) -> E): Parser<I, Boolean, E> =
   transformEither { input ->
      runCatching { input.toBooleanStrict() }.fold({ it.right() }, { ifError(input).leftNel() })
   }

/**
 * Transforms a nullable String producing [Parser] into a nullable Boolean producing Parser,
 * by converting the String to a Boolean using the library function [toBooleanStrictOrNull].
 */
fun <I, E> Parser<I, String, E>.booleanStrictOrNull(): Parser<I, Boolean?, E> =
   map { it.toBooleanStrictOrNull() }
