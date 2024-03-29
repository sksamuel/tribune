package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullTest : FunSpec() {
   init {

      test("withDefault on I -> A?") {
         val p = Parser<String>().nullIf { true }.withDefault { "wibble" }
         p.parse("abc") shouldBe "wibble".right()
      }

      test("nullIf on O -> A?") {
         val p = Parser<String>().nullIf { "foo" == it }
         p.parse("foo") shouldBe null.right()
         p.parse("bar") shouldBe "bar".right()
      }

      test("nullIf on O? -> A?") {
         val p = Parser<String?>().nullIf { "foo" == it }
         p.parse(null) shouldBe null.right()
         p.parse("foo") shouldBe null.right()
         p.parse("bar") shouldBe "bar".right()
      }

      test("withDefault on I? -> A?") {
         val p = Parser<String?>().withDefault { "wibble" }
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe "wibble".right()
      }

      test("nullable") {
         val p = Parser<String>().nullable()
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe null.right()
      }

      test("not null") {
         val p = Parser<String?>().notNull { "null not allowed" }
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe "null not allowed".leftNel()
      }

      test("replaces failure with null") {
         val p = Parser<String?>().failAsNull()
         p.parse("abc") shouldBe "abc".right()
         p.parse(null) shouldBe Either.Right(null)
      }

   }
}
