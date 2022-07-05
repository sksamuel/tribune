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

data class ParsedBook(
   val title: Title,
   val author: Author,
   val isbn: Isbn,
)

data class Title(val value: String)
data class Author(val value: String)
data class Isbn(val value: String)

val authorParser: Parser<String?, Author, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Author must be provided" }
      .filter({ it.contains(" ") }) { "Author must be at least two names" }
      .map { Author(it) }

val titleParser: Parser<String?, Title, String> =
   Parser.nonBlankString { "Title must be provided" }
      .map { Title(it) }

// must be 10 or 13 characters
val isbnParser: Parser<String?, Isbn, String> =
   Parser.from<String?>()
      .notNullOrBlank { "ISBN must be provided" }
      .length({ it == 10 || it == 13 }) { "Valid ISBNs have length 10 or 13" }
      .filter({ it.length == 10 || it.startsWith("9") }, { "13 Digit ISBNs must start with 9" })
      .map { Isbn(it) }

val bookParser: Parser<BookInput, ParsedBook, String> =
   Parser.compose(
      titleParser.contramap { it.title },
      authorParser.contramap { it.author },
      isbnParser.contramap { it.isbn },
      ::ParsedBook,
   )
