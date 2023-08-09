package com.sksamuel.tribune.core

import arrow.core.Validated
import arrow.core.validNel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullTest : FunSpec() {
   init {

      test("default") {
         val p = Parser<String?>().withDefault { "wibble" }
         p.parse("abc") shouldBe "abc".validNel()
         p.parse(null) shouldBe "wibble".validNel()
      }

      test("nullable") {
         val p = Parser<String>().allowNulls()
         p.parse("abc") shouldBe "abc".validNel()
         p.parse(null) shouldBe null.validNel()
      }

      test("not null") {
         val p = Parser<String?>().notNull { "null not allowed" }
         p.parse("abc") shouldBe "abc".validNel()
         p.parse(null) shouldBe Validated.invalidNel("null not allowed")
      }

      test("replaces failure with null") {
         val p = Parser<String?>().failAsNull()
         p.parse("abc") shouldBe "abc".validNel()
         p.parse(null) shouldBe Validated.validNel(null)
      }

   }
}
