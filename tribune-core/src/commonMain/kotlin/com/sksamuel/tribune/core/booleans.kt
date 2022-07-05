package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.validNel

fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.validNel() ?: ifError(it).invalidNel()
   }
