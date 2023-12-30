package com.sksamuel.tribune.core.collections

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.minlen
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SetTest : FunSpec() {
   init {

      data class ParsedString(val str: String)

      test("asSet") {
         val p = Parser<String>().map { ParsedString(it) }
         val pset: Parser<Collection<String>, Set<ParsedString>, Nothing> = p.asSet()
         pset.parse(listOf("a", "b")).getOrNull() shouldBe setOf(ParsedString("a"), ParsedString("b"))
      }

      test("asSet should accumulate errors") {
         val p = Parser<String>().minlen(2) { "whack $it" }.map { ParsedString(it) }
         val pset: Parser<Collection<String>, Set<ParsedString>, String> = p.asSet()
         pset.parse(listOf("a", "b")).leftOrNull() shouldBe listOf("whack a", "whack b")
      }

      test("filterNulls") {
         val p = Parser<String>().map { if (it == "a") null else ParsedString(it) }
         val pset: Parser<Collection<String>, Set<ParsedString>, String> = p.asSet().filterNulls()
         pset.parse(listOf("a", "b")).getOrNull() shouldBe setOf(ParsedString("b"))
      }
   }
}
