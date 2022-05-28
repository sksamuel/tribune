package com.sksamuel.princeps.core.parsers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullTest : FunSpec() {
   init {

      test("default") {
         val p = Parser<String?>().withDefault { "wibble" }
         p.parse("abc") shouldBe "abc".valid()
         p.parse(null) shouldBe "wibble".valid()
      }

      test("nullable") {
         val p = Parser<String>().allowNulls()
         p.parse("abc") shouldBe "abc".valid()
         p.parse(null) shouldBe null.valid()
      }
   }
}