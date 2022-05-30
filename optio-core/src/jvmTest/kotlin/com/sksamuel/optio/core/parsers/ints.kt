package com.sksamuel.optio.core.parsers

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.optio.core.Parser
import com.sksamuel.optio.core.allowNulls
import com.sksamuel.optio.core.getErrorsOrThrow
import com.sksamuel.optio.core.getOrThrow
import com.sksamuel.optio.core.inrange
import com.sksamuel.optio.core.int
import com.sksamuel.optio.core.long
import com.sksamuel.optio.core.negative
import com.sksamuel.optio.core.nonNegative
import com.sksamuel.optio.core.notNull
import com.sksamuel.optio.core.positive
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntTest : FunSpec() {
   init {

      test("in range") {
         val p = Parser<Int>().inrange(1..200) { "suck!" }
         p.parse(1) shouldBe 1.validNel()
         p.parse(200) shouldBe 200.validNel()
         p.parse(0) shouldBe "suck!".invalidNel()
         p.parse(201) shouldBe "suck!".invalidNel()
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
         p.parse("-1") shouldBe "must be >= 0".invalidNel()
         p.parse("0") shouldBe 0.validNel()
         p.parse("1") shouldBe 1.validNel()
      }

      test("positive") {
         val p = Parser<String>().int { "must be int" }.positive { "must be > 0" }
         p.parse("0") shouldBe "must be > 0".invalidNel()
         p.parse("-1") shouldBe "must be > 0".invalidNel()
         p.parse("1") shouldBe 1.validNel()
      }

      test("negative") {
         val p = Parser<String>().int { "must be int" }.negative { "must be < 0" }
         p.parse("0") shouldBe "must be < 0".invalidNel()
         p.parse("-1") shouldBe (-1).validNel()
         p.parse("1") shouldBe "must be < 0".invalidNel()
      }
   }
}
