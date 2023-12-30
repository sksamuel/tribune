package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right

inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.right() }, { ifError(symbol).leftNel() })
   }
}
