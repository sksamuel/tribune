package com.sksamuel.optio.core.parsers

/**
 * Chains a [Parser] to convert String -> Int.
 */
fun <I, E> Parser<I, String, E>.int(ifError: (String) -> E): Parser<I, Int, E> =
   flatMap {
      val i = it.toIntOrNull()
      i?.valid() ?: ifError(it).invalid()
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



