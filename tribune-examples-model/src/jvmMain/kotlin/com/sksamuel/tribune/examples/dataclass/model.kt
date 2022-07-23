package com.sksamuel.tribune.examples.dataclass

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.nonBlankString
import com.sksamuel.tribune.core.strings.notNullOrBlank
import com.sksamuel.tribune.examples.BookInput

data class Book internal constructor(
   val title: String,
   val author: String,
   val isbn: String,
)

val bookAuthorParser: Parser<String?, String, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Author must be provided" }
      .filter({ it.contains(" ") }) { "Author must be at least two names" }

val bookTitleParser: Parser<String?, String, String> =
   Parser.nonBlankString { "Title must be provided" }

// must be 10 or 13 characters
val bookIsbnParser: Parser<String?, String, String> =
   Parser.from<String?>()
      .notNullOrBlank { "ISBN must be provided" }
      .length({ it == 10 || it == 13 }) { "Valid ISBNs have length 10 or 13" }
      .filter({ it.length == 10 || it.startsWith("9") }, { "13 Digit ISBNs must start with 9" })

val bookParserDataclass: Parser<BookInput, Book, String> =
   Parser.compose(
      bookTitleParser.contramap { it.title },
      bookAuthorParser.contramap { it.author },
      bookIsbnParser.contramap { it.isbn },
      ::Book,
   )

