package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntTest : FunSpec() {
   init {

      test("in range") {
         val p = Parser<Int>().inrange(1..200) { "suck!" }
         p.parse(1) shouldBe 1.right()
         p.parse(200) shouldBe 200.right()
         p.parse(0) shouldBe "suck!".leftNel()
         p.parse(201) shouldBe "suck!".leftNel()
      }

      test("parser should support ints") {
         val p = Parser<String>().int { "not an int" }
         p.parse("foo").leftOrNull() shouldBe listOf("not an int")
         p.parse("12345").getOrNull() shouldBe 12345
      }

      test("parser should support ints with nullable pass through") {
         val p = Parser<String>().int { "not an int" }.allowNulls()
         p.parse("12345").getOrNull() shouldBe 12345
         p.parse(null).getOrNull() shouldBe null
      }

      test("parser should support ints with nullable failure message") {
         val p = Parser<String?>().notNull { "cannot be null" }.long { "not an int" }
         p.parse("12345").getOrNull() shouldBe 12345
         p.parse(null).leftOrNull() shouldBe listOf("cannot be null")
      }

      test("non neg") {
         val p = Parser<String>().int { "must be int" }.nonNegative { "must be >= 0" }
         p.parse("-1") shouldBe "must be >= 0".leftNel()
         p.parse("0") shouldBe 0.right()
         p.parse("1") shouldBe 1.right()
      }

      test("positive") {
         val p = Parser<String>().int { "must be int" }.positive { "must be > 0" }
         p.parse("0") shouldBe "must be > 0".leftNel()
         p.parse("-1") shouldBe "must be > 0".leftNel()
         p.parse("1") shouldBe 1.right()
      }

      test("negative") {
         val p = Parser<String>().int { "must be int" }.negative { "must be < 0" }
         p.parse("0") shouldBe "must be < 0".leftNel()
         p.parse("-1") shouldBe (-1).right()
         p.parse("1") shouldBe "must be < 0".leftNel()
      }
   }
}
