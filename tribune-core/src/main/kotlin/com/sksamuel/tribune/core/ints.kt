package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.validNel

/**
 * Chains a [Parser] to convert String -> Int.
 */
fun <I, E> Parser<I, String, E>.int(ifError: (String) -> E): Parser<I, Int, E> =
   flatMap {
      val i = it.toIntOrNull()
      i?.validNel() ?: ifError(it).invalidNel()
   }

/**
 * Chains a [Parser] to convert String? -> positive Int.
 */
fun <I, E> Parser<I, Int, E>.positive(ifError: (Int) -> E): Parser<I, Int, E> =
   this.filter({ it > 0 }, ifError)

/**
 * Chains a [Parser] to convert String? -> non-negative Int.
 */
fun <I, E> Parser<I, Int, E>.nonNegative(ifError: (Int) -> E): Parser<I, Int, E> =
   this.filter({ it >= 0 }, ifError)


fun <I, E> Parser<I, Int, E>.negative(ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it < 0) it.validNel() else ifError(it).invalidNel()
   }

fun <I, E> Parser<I, Int, E>.inrange(range: IntRange, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it in range) it.validNel() else ifError(it).invalidNel()
   }

fun <I, E> Parser<I, Int, E>.min(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.validNel() else ifError(it).invalidNel()
   }

fun <I, E> Parser<I, Int, E>.max(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.validNel() else ifError(it).invalidNel()
   }



