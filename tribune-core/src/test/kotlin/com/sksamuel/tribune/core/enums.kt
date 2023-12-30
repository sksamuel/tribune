package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.enums.enum
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EnumTest : FunSpec() {

   enum class EnumUnderTest {
      A, B
   }

   init {

      val p: Parser<String, EnumUnderTest, String> = Parser<String>().enum { "not an enum" }

      test("parser should support valid enum") {
         p.parse("A") shouldBe EnumUnderTest.A.right()
      }

      test("alt enum syntax") {
         Parser<String>().enum(EnumUnderTest::class) { "not an enum" }.parse("B") shouldBe EnumUnderTest.B.right()
      }

      test("parser should support invalid enum") {
         val enum = p.parse("a")
         enum shouldBe "not an enum".leftNel()
      }

      test("parser should support nulls") {
         Parsers.nullableString.enum<String?, EnumUnderTest, String> { "error" }.parse(null) shouldBe Either.Right(null)
         Parsers.nullableString.enum(EnumUnderTest::class) { "error" }.parse(null) shouldBe Either.Right(null)
      }
   }
}
