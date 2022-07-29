package com.sksamuel.tribune.examples.opaque_valueclass

data class ParsedBook(
   val title: Title,
   val author: Author,
   val isbn: Isbn,
)

@JvmInline
value class Title internal constructor(private val value: String) {
   val asString get() = value
}

@JvmInline
value class Author internal constructor(private val value: String) {
   val asString get() = value
}

@JvmInline
value class Isbn internal constructor(private val value: String) {
   val asString get() = value
}

