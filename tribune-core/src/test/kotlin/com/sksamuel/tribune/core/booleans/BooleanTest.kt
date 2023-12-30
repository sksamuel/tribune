package com.sksamuel.tribune.core.booleans

import com.sksamuel.tribune.core.Parser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BooleanTest : FunSpec() {
   init {
      test("parser should support String -> Boolean") {
         val p = Parser<String>().boolean()
         p.parse("foo").getOrNull() shouldBe false
         p.parse("true").getOrNull() shouldBe true
         p.parse("false").getOrNull() shouldBe false
      }

      test("parser should support String -> Boolean using strict mode") {
         val p = Parser<String>().booleanStrict { "not a boolean" }
         p.parse("foo").leftOrNull() shouldBe listOf("not a boolean")
         p.parse("true").getOrNull() shouldBe true
         p.parse("false").getOrNull() shouldBe false
      }

      test("parser should support String -> Boolean? using strict mode or null") {
         val p = Parser<String>().booleanStrictOrNull()
         p.parse("foo").getOrNull() shouldBe null
         p.parse("true").getOrNull() shouldBe true
         p.parse("false").getOrNull() shouldBe false
      }
   }
}
