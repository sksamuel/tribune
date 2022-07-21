package com.sksamuel.tribune.examples

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.nonBlankString
import com.sksamuel.tribune.core.strings.notNullOrBlank

data class BookInput(
   val title: String?,
   val author: String?,
   val isbn: String?,
)

val bookParser: Parser<BookInput, ParsedBook, String> =
   Parser.compose(
      titleParser.contramap { it.title },
      authorParser.contramap { it.author },
      isbnParser.contramap { it.isbn },
      ::ParsedBook,
   )
