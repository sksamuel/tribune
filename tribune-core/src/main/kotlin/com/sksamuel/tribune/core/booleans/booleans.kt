package com.sksamuel.tribune.core.booleans

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap

fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.right() ?: ifError(it).leftNel()
   }
