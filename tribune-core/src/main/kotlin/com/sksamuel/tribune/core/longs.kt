package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right

/**
 * Extends a [Parser] of output type string to parse that string into a long.
 * If the string cannot be parsed into a long, then the error is generated by the
 * given function [ifError].
 *
 * Note: This parser accepts nullable inputs if the receiver accepts nullable inputs
 * and a null is considered a failing case.
 */
fun <I, E> Parser<I, String, E>.long(ifError: (String) -> E): Parser<I, Long, E> =
   flatMap {
      val l = it.toLongOrNull()
      l?.right() ?: ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.inrange(range: LongRange, ifError: (Long) -> E): Parser<I, Long, E> =
   flatMap {
      if (it in range) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.min(min: Long, ifError: (Long) -> E): Parser<I, Long, E> =
   flatMap {
      if (it >= min) it.right() else ifError(it).leftNel()
   }

fun <I, E> Parser<I, Long, E>.max(min: Long, ifError: (Long) -> E): Parser<I, Long, E> =
   flatMap {
      if (it >= min) it.right() else ifError(it).leftNel()
   }
