package com.sksamuel.tribune.datetime

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.flatMap
import kotlinx.datetime.LocalDateTime

/**
 * Maps a [Parser] that returns a [String] to return a [LocalDateTime] if the input is
 * an iso-standard local date time.
 */
fun <I, E> Parser<I, String, E>.toLocalTime(
   ifError: (String, Throwable) -> E
): Parser<I, LocalDateTime, E> = flatMap {
   try {
      LocalDateTime.parse(it).validNel()
   } catch (t: Throwable) {
      ifError(it, t).invalidNel()
   }
}
