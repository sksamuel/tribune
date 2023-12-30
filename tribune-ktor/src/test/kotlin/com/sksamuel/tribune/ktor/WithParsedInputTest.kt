package com.sksamuel.tribune.ktor

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.Parsers
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.filter
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.notNullOrBlank
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.testing.testApplication

class WithParsedInputTest : FunSpec() {
   init {
      test("happy path should pass off to lambda") {
         testApplication {
            install(ContentNegotiation) { jackson() }
            routing {
               post("/foo") {
                  withParsedBody(bookParser, jsonHandler) {
                     call.respond(HttpStatusCode.Created, "Book created")
                  }
               }
            }
            val resp = client.post("/foo") {
               contentType(ContentType.Application.Json)
               setBody("""{ "author": "Willy Shakes", "title": "midwinters day dream", "isbn": "1234567890" }""")
            }
            resp.status shouldBe HttpStatusCode.Created
         }
      }

      test("unhappy path with textPlainHandler handler") {
         testApplication {
            install(ContentNegotiation) { jackson() }
            routing {
               post("/foo") {
                  withParsedBody(bookParser, textPlainHandler) {
                     call.respond(HttpStatusCode.Created, "Book created")
                  }
               }
            }
            val resp = client.post("/foo") {
               contentType(ContentType.Application.Json)
               setBody("""{ "author": "Willy Shakes", "isbn": "123" }""")
            }
            resp.status shouldBe HttpStatusCode.BadRequest
            resp.bodyAsText() shouldBe """Title must be provided, Valid ISBNs have length 10 or 13"""
         }
      }

      test("unhappy path with json handler") {
         testApplication {
            install(ContentNegotiation) { jackson() }
            routing {
               post("/foo") {
                  withParsedBody(bookParser, jsonHandler) {
                     call.respond(HttpStatusCode.Created, "Book created")
                  }
               }
            }
            val resp = client.post("/foo") {
               contentType(ContentType.Application.Json)
               setBody("""{ "author": "Willy Shakes", "isbn": "123" }""")
            }
            resp.status shouldBe HttpStatusCode.BadRequest
            resp.bodyAsText() shouldBe """[
"Title must be provided",
"Valid ISBNs have length 10 or 13"
]"""
         }
      }
   }
}

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

// must be at least two tokens
val authorParser: Parser<String?, Author, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Author must be provided" }
      .filter({ it.contains(" ") }) { "Author must be at least two names" }
      .map { Author(it) }

val titleParser: Parser<String?, Title, String> =
   Parsers.nonBlankString { "Title must be provided" }
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
