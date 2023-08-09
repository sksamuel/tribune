package com.sksamuel.tribune.examples

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.nonBlankString
import com.sksamuel.tribune.core.strings.notNullOrBlank
import com.sksamuel.tribune.examples.opaque_dataclass.Book
import com.sksamuel.tribune.examples.opaque_valueclass.Author
import com.sksamuel.tribune.examples.opaque_valueclass.Isbn
import com.sksamuel.tribune.examples.opaque_valueclass.ParsedBook
import com.sksamuel.tribune.examples.opaque_valueclass.Title

// parsers for data class

val bookAuthorParser: Parser<String?, String, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Author must be provided" }
      .filter({
         val words = it.split(' ')
         words.size > 1&& words.last().isNotBlank()
      }) { "Author must be at least two names" }

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

// parsers for value classes

val authorParser: Parser<String?, Author, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Author must be provided" }
      .filter({
         val words = it.split(' ')
         words.size > 1&& words.last().isNotBlank()
      }) { "Author must be at least two names" }
      .map { Author(it) }

val titleParser: Parser<String?, Title, String> =
   Parser.nonBlankString { "Title must be provided" }
      .map { Title(it) }

// must be 10 or 13 characters
val isbnParser = Parser.fromNullableString()
   .notNullOrBlank { "ISBN must be provided" }
   .map { it.replace("-", "") }
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
