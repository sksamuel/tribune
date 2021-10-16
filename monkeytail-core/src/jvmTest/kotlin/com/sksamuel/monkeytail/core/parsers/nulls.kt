package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.valid
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullTest : FunSpec() {
   init {

      test("default") {
         val p = Parser<String?>().default { "wibble" }
         p.parse("abc") shouldBe "abc".valid()
         p.parse(null) shouldBe "wibble".valid()
      }

      test("nullable") {
         val p = Parser<String>().nullable()
         p.parse("abc") shouldBe "abc".valid()
         p.parse(null) shouldBe null.valid()
      }
   }
}
