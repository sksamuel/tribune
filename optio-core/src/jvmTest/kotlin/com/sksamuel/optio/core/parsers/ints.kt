package com.sksamuel.optio.core.parsers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntTest : FunSpec() {
   init {

      test("in range") {
         val p = Parser<Int>().inrange(1..200) { "suck!" }
         p.parse(1) shouldBe 1.valid()
         p.parse(200) shouldBe 200.valid()
         p.parse(0) shouldBe "suck!".invalid()
         p.parse(201) shouldBe "suck!".invalid()
      }

      test("parser should support ints") {
         val p = Parser<String>().long { "not an int" }
         p.parse("foo").getErrorsOrThrow() shouldBe listOf("not an int")
         p.parse("12345").getOrThrow() shouldBe 12345
      }

      test("parser should support ints with nullable pass through") {
         val p = Parser<String>().long { "not an int" }.allowNulls()
         p.parse("12345").getOrThrow() shouldBe 12345
         p.parse(null).getOrThrow() shouldBe null
      }

      test("parser should support ints with nullable failure message") {
         val p = Parser<String?>().notNull { "cannot be null" }.long { "not an int" }
         p.parse("12345").getOrThrow() shouldBe 12345
         p.parse(null).getErrorsOrThrow() shouldBe listOf("cannot be null")
      }

      test("non neg") {
         val p = Parser<String>().int { "must be int" }.nonNegative { "must be >= 0" }
         p.parse("-1") shouldBe "must be >= 0".invalid()
         p.parse("0") shouldBe 0.valid()
         p.parse("1") shouldBe 1.valid()
      }

      test("positive") {
         val p = Parser<String>().int { "must be int" }.positive { "must be > 0" }
         p.parse("0") shouldBe "must be > 0".invalid()
         p.parse("-1") shouldBe "must be > 0".invalid()
         p.parse("1") shouldBe 1.valid()
      }

      test("negative") {
         val p = Parser<String>().int { "must be int" }.negative { "must be < 0" }
         p.parse("0") shouldBe "must be < 0".invalid()
         p.parse("-1") shouldBe (-1).valid()
         p.parse("1") shouldBe "must be < 0".invalid()
      }
   }
}
