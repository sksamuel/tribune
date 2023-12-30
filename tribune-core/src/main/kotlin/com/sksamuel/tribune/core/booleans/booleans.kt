package com.sksamuel.tribune.core.booleans

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap

/**
 * Transforms a [Parser] that produces Strings by converting the string to a Boolean using [toBoolean].
 */
fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBoolean()
      b.right()
   }

/**
 * Transforms a [Parser] that produces Strings by converting the string to a Boolean using [toBooleanStrict].
 */
fun <I, E> Parser<I, String, E>.booleanStrict(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrict()
      b.right()
   }

/**
 * Transforms a [Parser] that produces Strings by converting the string to a Boolean using [toBooleanStrictOrNull].
 */
fun <I, E> Parser<I, String, E>.booleanStrictOrNull(ifError: (String) -> E): Parser<I, Boolean?, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.right() ?: ifError(it).leftNel()
   }
