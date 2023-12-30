package com.sksamuel.tribune.core

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.floats.float
import com.sksamuel.tribune.core.ints.int
import com.sksamuel.tribune.core.longs.long
import com.sksamuel.tribune.core.strings.minlen
import com.sksamuel.tribune.core.strings.notBlank
import com.sksamuel.tribune.core.strings.notNullOrBlank
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@JvmInline
value class Foo(val value: String)

@JvmInline
value class Width(val value: Double)

class ValidatedTest : FunSpec() {
   init {

      test("parsing strings to domain object") {
         Parser<String>().parse("input").map { Foo("input") } shouldBe Foo("input").right()
      }

      test("parsing non blank strings to domain object") {
         Parser<String>()
            .notBlank { "cannot be blank" }
            .map { Foo("input") }
            .parse("    ") shouldBe "cannot be blank".leftNel()
      }

      test("parser should support default") {
         val p = Parser<String>()
            .mapIfNotNull { Foo(it) }
            .withDefault { Foo("foo") }
         val result: EitherNel<Nothing, Foo> = p.parse("foo")
      }

      test("parser should support default on nullable inputs") {
         val p = Parser<String?>()
            .mapIfNotNull { Foo(it) }
            .withDefault { Foo("foo") }
         val result: EitherNel<Nothing, Foo> = p.parse("foo")
      }

      test("parser should support longs") {
         val p = Parser<String>().long { "not a long" }
         p.parse("foo").leftOrNull() shouldBe listOf("not a long")
         p.parse("12345").getOrNull() shouldBe 12345L
      }

      test("parser should support floats") {
         val p = Parser<String>().float { "not a float" }
         p.parse("foo").leftOrNull() shouldBe listOf("not a float")
         p.parse("123.45").getOrNull() shouldBe 123.45F
      }

      test("filter") {
         val p = Parser<String>().filter(String::isNotEmpty) { "boom" }.map { Foo(it) }
         p.parse("") shouldBe "boom".leftNel()
         p.parse("abc") shouldBe Foo("abc").right()
      }

      test("contains") {
         val p = Parser<String>().oneOf(listOf("a", "b")) { "boom" }.map { Foo(it) }
         p.parse("") shouldBe "boom".leftNel()
         p.parse("c") shouldBe "boom".leftNel()
         p.parse("a") shouldBe Foo("a").right()
         p.parse("b") shouldBe Foo("b").right()
      }

      test("composite parser") {
         data class UpdateRequest(val name: String, val age: String?)
         data class ValidName(val name: String)
         data class ValidAge(val age: Int)

         val nameParser: Parser<UpdateRequest, ValidName, String> = Parser<UpdateRequest>()
            .map { it.name }
            .notNullOrBlank { "Name cannot be blank" }
            .minlen(6) { "Name must have 6 characters" }
            .map { ValidName(it) }

         val ageParser: Parser<UpdateRequest, ValidAge, String> = Parser<UpdateRequest>()
            .map { it.age }
            .notNull { "Age cannot be null" }
            .int { "Age must be a number" }
            .map { ValidAge(it) }

         val parser: Parser<UpdateRequest, Pair<ValidName, ValidAge>, String> = Parser.zip(nameParser, ageParser)
         val result = parser.parse(UpdateRequest("a", "b"))
      }
   }
}
