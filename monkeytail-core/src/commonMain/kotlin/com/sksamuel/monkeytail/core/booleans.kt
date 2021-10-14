package com.sksamuel.monkeytail.core

import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid

fun <I, E> Parser<I, String, E>.boolean(ifError: (String) -> E): Parser<I, Boolean, E> =
   flatMap {
      val b = it.toBooleanStrictOrNull()
      b?.valid() ?: ifError(it).invalid()
   }
