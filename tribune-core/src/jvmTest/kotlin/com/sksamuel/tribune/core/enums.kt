package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.validNel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EnumTest : FunSpec() {

   enum class EnumUnderTest {
      A, B, C
   }

   init {

      val p: Parser<String, EnumUnderTest, String> = Parser<String>().enum { "not an enum" }

      test("parser should support valid enum") {
         p.parse("A") shouldBe EnumUnderTest.A.validNel()
      }

      test("parser should support invalid enum") {
         val enum = p.parse("a")
         enum shouldBe "not an enum".invalidNel()
      }
   }
}
