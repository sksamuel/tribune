package com.sksamuel.optio.core.collections

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.optio.core.Foo
import com.sksamuel.optio.core.Parser
import com.sksamuel.optio.core.map
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ListTest : FunSpec() {
   init {

      test("asList") {
         val ps: Parser<List<String>, List<Foo>, Nothing> = Parser<String>().map { Foo(it) }.asList()
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).validNel()
      }

      test("asList with min length") {
         val ps = Parser<String>().map { Foo(it) }.asList(min = 2) { "Must have at least two elements" }
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).validNel()
         ps.parse(listOf("a")) shouldBe "Must have at least two elements".invalidNel()
      }
   }
}
