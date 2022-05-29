package com.sksamuel.optio.datetime

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.optio.core.parsers.Parser
import com.sksamuel.optio.core.parsers.flatMap
import kotlinx.datetime.LocalDateTime

/**
 * Maps a [Parser] that returns a [String] to return a [LocalDateTime] if the input is an iso-standard local date.
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
