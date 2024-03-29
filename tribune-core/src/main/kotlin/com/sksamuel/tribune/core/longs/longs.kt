package com.sksamuel.tribune.core.longs

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.transformEither

/**
 * Extends a [Parser] of output type string to parse that string into a long.
 * If the string cannot be parsed into a long, then the error is generated by the
 * given function [ifError].
 *
 * Note: This parser accepts nullable inputs if the receiver accepts nullable inputs
 * and a null is considered a failing case.
 */
fun <I, E> Parser<I, String, E>.long(ifError: (String) -> E): Parser<I, Long, E> =
   transformEither {
      val l = it.toLongOrNull()
      l?.right() ?: ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.inrange(range: LongRange, ifError: (Long) -> E): Parser<I, Long, E> =
   transformEither {
      if (it in range) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.min(min: Long, ifError: (Long) -> E): Parser<I, Long, E> =
   transformEither {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.max(min: Long, ifError: (Long) -> E): Parser<I, Long, E> =
   transformEither {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

@JvmName("minOrNull")
fun <I, E> Parser<I, Long?, E>.min(min: Long, ifError: (Long) -> E): Parser<I, Long?, E> =
   transformEither {
      if (it == null) Either.Right(null) else if (it >= min) it.right() else ifError(it).leftNel()
   }

@JvmName("maxOrNull")
fun <I, E> Parser<I, Long?, E>.max(min: Long, ifError: (Long) -> E): Parser<I, Long?, E> =
   transformEither {
      if (it == null) Either.Right(null) else if (it >= min) it.right() else ifError(it).leftNel()
   }
