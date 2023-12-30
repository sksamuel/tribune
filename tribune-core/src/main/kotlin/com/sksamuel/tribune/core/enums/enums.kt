package com.sksamuel.tribune.core.enums

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap

/**
 * Wraps a [Parser] that produces Strings to one that produces a value of a given enum.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 */
inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.right() }, { ifError(symbol).leftNel() })
   }
}

/**
 * Wraps a [Parser] that produces nullable Strings to one that produces nullable enums.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 * If the String is null, then null is produced.
 */
inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String?, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM?, E> {
   return flatMap { symbol ->
      if (symbol == null) Either.Right(null)
      else runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.right() }, { ifError(symbol).leftNel() })
   }
}
