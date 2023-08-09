package com.sksamuel.tribune.core

import com.sksamuel.tribune.core.strings.notNullOrBlank

object Parsers {

   val nullableString: Parser<String?, String?, Nothing> = Parser.fromNullableString()

   fun <I, E> notNull(error: () -> E): Parser<I?, I, E> = Parser.invoke<I?>().notNull { error() }

   fun <E> nonBlankString(error: () -> E): Parser<String?, String, E> =
      Parser.from<String?>().notNullOrBlank { error() }
}
