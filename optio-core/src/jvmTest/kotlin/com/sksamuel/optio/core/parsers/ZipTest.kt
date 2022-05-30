package com.sksamuel.optio.core.parsers

import arrow.core.invalidNel
import arrow.core.nonEmptyListOf
import arrow.core.validNel
import com.sksamuel.optio.core.Parser
import com.sksamuel.optio.core.strings.minlen
import com.sksamuel.optio.core.strings.notNullOrBlank
import com.sksamuel.optio.core.zip
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ZipTest : FunSpec() {
   init {
      test("zip 2") {
         val p1 = Parser.from<String?>().notNullOrBlank { "foo" }.minlen(2) { "Must have length > 2" }
         val p2 = Parser.from<String?>().notNullOrBlank { "bar" }
         val p = Parser.zip(p1, p2)
         p.parse(null) shouldBe nonEmptyListOf("foo", "bar").invalidNel()
         p.parse("a") shouldBe nonEmptyListOf("Must have length > 2").invalidNel()
         p.parse("ab") shouldBe Pair("ab", "ab").validNel()
      }

      test("zip 3") {
         val p1 = Parser.from<String?>().notNullOrBlank { "foo" }.minlen(2) { "Must have length > 2" }
         val p2 = Parser.from<String?>().notNullOrBlank { "bar" }
         val p3 = Parser.from<String?>().notNullOrBlank { "baz" }
         val p = Parser.zip(p1, p2, p3)
         p.parse(null) shouldBe nonEmptyListOf("foo", "bar", "baz").invalidNel()
         p.parse("a") shouldBe nonEmptyListOf("Must have length > 2").invalidNel()
         p.parse("ab") shouldBe Triple("ab", "ab", "ab").validNel()
      }
   }
}
