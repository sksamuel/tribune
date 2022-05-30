package com.sksamuel.optio.core

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
   }
}
