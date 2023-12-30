package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right

fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.right() ?: ifError(it).leftNel()
   }
