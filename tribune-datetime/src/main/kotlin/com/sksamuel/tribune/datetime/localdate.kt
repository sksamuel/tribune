package com.sksamuel.tribune.datetime

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.transformEither
import kotlinx.datetime.LocalDate

/**
 * Maps a [Parser] that returns a [String] to return a [LocalDate] if the input is an iso-standard local date.
 */
fun <I, E> Parser<I, String, E>.toLocalDate(
   ifError: (String, Throwable) -> E
): Parser<I, LocalDate, E> = transformEither {
   try {
      LocalDate.parse(it).right()
   } catch (t: Throwable) {
      ifError(it, t).leftNel()
   }
}
