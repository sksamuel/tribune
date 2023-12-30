package com.sksamuel.tribune.core

import arrow.core.leftNel
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleTest : FunSpec() {
   init {

      test("in range") {
         val p = Parser<Double>().inrange(1.0..200.0) { "suck!" }
         p.parse(1.0) shouldBe 1.0.right()
         p.parse(200.0) shouldBe 200.0.right()
         p.parse(0.0) shouldBe "suck!".leftNel()
         p.parse(201.0) shouldBe "suck!".leftNel()
      }

      test("parser should support converting to doubles from strings") {
         val p = Parser<String>().double { "not a double" }.map { Width(it) }
         p.parse("foo").getOrNull() shouldBe listOf("not a double")
         p.parse("123.45").getOrNull() shouldBe Width(123.45)
      }

      test("parser should support doubles with nullable pass through") {
         val p = Parser<String>().double { "not a double" }.allowNulls()
         p.parse("123.45").getOrNull() shouldBe 123.45
         p.parse(null).getOrNull() shouldBe null
      }

      test("parser should support doubles with nullable failure message") {
         val p = Parser<String?>().notNull { "cannot be null" }.double { "not a double" }
         p.parse("123.45").getOrNull() shouldBe 123.45
         p.parse(null).getOrNull() shouldBe listOf("cannot be null")
      }

      test("non neg") {
         val p = Parser<String>().double { "must be int" }.nonNegative { "must be >= 0" }
         p.parse("-1") shouldBe "must be >= 0".leftNel()
         p.parse("0") shouldBe 0.0.right()
         p.parse("1") shouldBe 1.0.right()
      }

      test("positive") {
         val p = Parser<String>().double { "must be int" }.positive { "must be > 0" }
         p.parse("0") shouldBe "must be > 0".leftNel()
         p.parse("-1") shouldBe "must be > 0".leftNel()
         p.parse("1") shouldBe 1.0.right()
      }

      test("negative") {
         val p = Parser<String>().double { "must be int" }.negative { "must be < 0" }
         p.parse("0") shouldBe "must be < 0".leftNel()
         p.parse("-1") shouldBe (-1.0).right()
         p.parse("1") shouldBe "must be < 0".leftNel()
      }
   }
}
