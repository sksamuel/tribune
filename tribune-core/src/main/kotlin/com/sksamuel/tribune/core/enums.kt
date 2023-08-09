package com.sksamuel.tribune.core

import arrow.core.*

inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.validNel() }, { ifError(symbol).invalidNel() })
   }
}

/*
inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<String, Set<ENUM>, E>.inset(
   set: Set<ENUM>,
   ifError: (Int) -> E
): Parser<String, ENUM, E> {
   val p = Parser<String>().enum { "" }
   flatMap { str ->
      p.parse(str)
   }
}
*/

