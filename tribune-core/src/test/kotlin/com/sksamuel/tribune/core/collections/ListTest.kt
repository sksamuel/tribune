package com.sksamuel.tribune.core.collections

import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.minlen
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ListTest : FunSpec() {
   init {

      data class ParsedString(val str: String)

      test("asList") {
         val p = Parser<String>().map { ParsedString(it) }
         val plist: Parser<Collection<String>, List<ParsedString>, Nothing> = p.asList()
         plist.parse(listOf("a", "b")).getOrNull() shouldBe listOf(ParsedString("a"), ParsedString("b"))
      }

      test("asList should accumulate errors") {
         val p = Parser<String>().minlen(2) { "whack $it" }.map { ParsedString(it) }
         val plist: Parser<Collection<String>, List<ParsedString>, String> = p.asList()
         plist.parse(listOf("a", "b")).leftOrNull() shouldBe listOf("whack a", "whack b")
      }

      test("filterNulls") {
         val p = Parser<String>().map { if (it == "a") null else ParsedString(it) }
         val plist: Parser<Collection<String>, List<ParsedString>, String> = p.asList().filterNulls()
         plist.parse(listOf("a", "b")).getOrNull() shouldBe listOf(ParsedString("b"))
      }
   }
}
