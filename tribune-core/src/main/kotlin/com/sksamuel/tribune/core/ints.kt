package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.leftNel
import arrow.core.right
import arrow.core.validNel

/**
 * Chains a [Parser] to convert String -> Int.
 */
fun <I, E> Parser<I, String, E>.int(ifError: (String) -> E): Parser<I, Int, E> =
   flatMap {
      val i = it.toIntOrNull()
      i?.right() ?: ifError(it).leftNel()
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
      if (it < 0) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.inrange(range: IntRange, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it in range) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.min(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.max(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   flatMap {
      if (it >= min) it.right() else ifError(it).leftNel()
   }



