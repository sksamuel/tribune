package com.sksamuel.tribune.core.collections

import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Foo
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ListTest : FunSpec() {
   init {

      test("asList") {
         val ps: Parser<List<String>, List<Foo>, Nothing> = Parser<String>().map { Foo(it) }.asList()
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).right()
      }

      test("asList with min length") {
         val ps = Parser<String>().map { Foo(it) }.asList(min = 2) { "Must have at least two elements" }
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).right()
         ps.parse(listOf("a")) shouldBe "Must have at least two elements".leftNel()
      }
   }
}
