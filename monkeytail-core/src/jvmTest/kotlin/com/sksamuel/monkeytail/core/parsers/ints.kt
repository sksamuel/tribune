package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid
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
   }
}
