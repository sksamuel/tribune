package com.sksamuel.princeps.core.parsers

fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.valid() ?: ifError(it).invalid()
   }
