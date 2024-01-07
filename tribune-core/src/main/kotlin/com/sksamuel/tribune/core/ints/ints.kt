package com.sksamuel.tribune.core.ints

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.transformEither

/**
 * Chains a [Parser] to convert String -> Int.
 */
fun <I, E> Parser<I, String, E>.int(ifError: (String) -> E): Parser<I, Int, E> =
   transformEither {
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
   transformEither {
      if (it < 0) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.inrange(range: IntRange, ifError: (Int) -> E): Parser<I, Int, E> =
   transformEither {
      if (it in range) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.min(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   transformEither {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

@JvmName("minOrNull")
fun <I, E> Parser<I, Int?, E>.min(min: Int, ifError: (Int) -> E): Parser<I, Int?, E> =
   transformEither {
      if (it == null) Either.Right(null) else if (it >= min) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Int, E>.max(min: Int, ifError: (Int) -> E): Parser<I, Int, E> =
   transformEither {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

@JvmName("maxOrNull")
fun <I, E> Parser<I, Int?, E>.max(min: Int, ifError: (Int) -> E): Parser<I, Int?, E> =
   transformEither {
      if (it == null) Either.Right(null) else if (it >= min) it.right() else ifError(it).leftNel()
   }
