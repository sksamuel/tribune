package com.sksamuel.tribune.examples

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.tribune.examples.valueclass.Author
import com.sksamuel.tribune.examples.valueclass.Isbn
import com.sksamuel.tribune.examples.valueclass.Title
import com.sksamuel.tribune.examples.valueclass.authorParser
import com.sksamuel.tribune.examples.valueclass.isbnParser
import com.sksamuel.tribune.examples.valueclass.titleParser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BookTest : FunSpec() {
   init {

      test("parses author") {
         authorParser.parse("John Doe") shouldBe Author("John Doe").validNel()
      }

      test("parses title") {
         titleParser.parse("About something") shouldBe Title("About something").validNel()
      }

      test("parses isbn") {
         isbnParser.parse("0123456789") shouldBe Isbn("0123456789").validNel()
      }

      test("parses invalid isbn") {
         isbnParser.parse("0123456789123") shouldBe "13 Digit ISBNs must start with 9".invalidNel()
      }

   }
}

