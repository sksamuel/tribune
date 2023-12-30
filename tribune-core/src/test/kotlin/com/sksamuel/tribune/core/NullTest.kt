package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import arrow.core.validNel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullTest : FunSpec() {
   init {

      test("default") {
         val p = Parser<String?>().withDefault { "wibble" }
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe "wibble".right()
      }

      test("nullable") {
         val p = Parser<String>().allowNulls()
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe null.right()
      }

      test("not null") {
         val p = Parser<String?>().notNull { "null not allowed" }
         p.parse("abc") shouldBe "abc".validNel()
         p.parse(null) shouldBe "null not allowed".leftNel()
      }

      test("replaces failure with null") {
         val p = Parser<String?>().failAsNull()
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe Either.Right(null)
      }

   }
}
