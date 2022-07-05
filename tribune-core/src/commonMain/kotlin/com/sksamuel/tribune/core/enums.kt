package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.validNel

inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.validNel() }, { ifError(symbol).invalidNel() })
   }
}
