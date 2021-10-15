package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid

inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.valid() }, { ifError(symbol).invalid() })
   }
}
