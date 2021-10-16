package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid

/**
 * Chains a [Parser] to convert String? -> Int.
 * An input of type
 */
fun <I, E> Parser<I, String, E>.int(ifError: (String) -> E): Parser<I, Int, E> =
   flatMap {
      val i = it.toIntOrNull()
      i?.valid() ?: ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.positive(ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it > 0) it.valid() else ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.nonneg(ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= 0) it.valid() else ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.negative(ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it < 0) it.valid() else ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.inrange(range: IntRange, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it in range) it.valid() else ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.min(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.valid() else ifError(it).invalid()
   }

fun <I, E> Parser<I, Int, E>.max(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.valid() else ifError(it).invalid()
   }



