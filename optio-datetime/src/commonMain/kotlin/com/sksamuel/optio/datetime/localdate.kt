package com.sksamuel.optio.datetime

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.optio.core.parsers.Parser
import com.sksamuel.optio.core.parsers.flatMap
import kotlinx.datetime.LocalDate

/**
 * Maps a [Parser] that returns a [String] to return a [LocalDate] if the input is an iso-standard local date.
 */
fun <I, E> Parser<I, String, E>.toLocalDate(
   ifError: (String, Throwable) -> E
): Parser<I, LocalDate, E> = flatMap {
   try {
      LocalDate.parse(it).validNel()
   } catch (t: Throwable) {
      ifError(it, t).invalidNel()
   }
}
