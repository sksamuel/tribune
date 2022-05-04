package com.sksamuel.princeps.core.parsers

inline fun <I, E, reified ENUM : Enum<ENUM>> Parser<I, String, E>.enum(crossinline ifError: (String) -> E): Parser<I, ENUM, E> {
   return flatMap { symbol ->
      runCatching { enumValueOf<ENUM>(symbol) }
         .fold({ it.valid() }, { ifError(symbol).invalid() })
   }
}
