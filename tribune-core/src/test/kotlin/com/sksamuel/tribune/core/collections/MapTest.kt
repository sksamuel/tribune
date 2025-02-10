package com.sksamuel.tribune.core.collections

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.maps.parseKeys
import com.sksamuel.tribune.core.maps.parseValues
import com.sksamuel.tribune.core.strings.minlen
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapTest : FunSpec() {
    init {
        data class ParsedString(val str: String)

        test("parseKeys") {
            val p = Parser<String>().map { ParsedString(it) }
            val pmap = Parser.from<Map<String, String>>().parseKeys(p)
            pmap.parse(mapOf("a" to "b", "c" to "d")).getOrNull() shouldBe mapOf(ParsedString("a") to "b", ParsedString("c") to "d")
        }

        test("parseKeys should short-circuit errors") {
            val p = Parser<String>().minlen(2) { "whack $it" }.map { ParsedString(it) }
            val pmap = Parser.from<Map<String, String>>().parseKeys(p)
            pmap.parse(mapOf("a" to "b", "c" to "d")).leftOrNull() shouldBe listOf("whack a")
        }

        test("parseValues") {
            val p = Parser<String>().map { ParsedString(it) }
            val pmap = Parser.from<Map<String, String>>().parseValues(p)
            pmap.parse(mapOf("a" to "b", "c" to "d")).getOrNull() shouldBe mapOf("a" to ParsedString("b"), "c" to ParsedString("d"))
        }

        test("parseValues should short-circuit errors") {
            val p = Parser<String>().minlen(2) { "whack $it" }.map { ParsedString(it) }
            val pmap = Parser.from<Map<String, String>>().parseValues(p)
            pmap.parse(mapOf("a" to "b", "c" to "d")).leftOrNull() shouldBe listOf("whack b")
        }
    }
}