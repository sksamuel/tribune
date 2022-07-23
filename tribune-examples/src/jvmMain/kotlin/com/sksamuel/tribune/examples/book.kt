package com.sksamuel.tribune.examples

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.examples.opaque.value.ParsedBook
import com.sksamuel.tribune.examples.opaque.value.authorParser
import com.sksamuel.tribune.examples.opaque.value.isbnParser
import com.sksamuel.tribune.examples.opaque.value.titleParser

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
