package com.sksamuel.optio.core.parsers

import arrow.core.Validated
import com.sksamuel.optio.core.parsers.Parser
import com.sksamuel.optio.core.parsers.filter
import com.sksamuel.optio.core.parsers.getErrorsOrThrow
import com.sksamuel.optio.core.parsers.getOrThrow
import com.sksamuel.optio.core.parsers.invalid
import com.sksamuel.optio.core.parsers.valid
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@JvmInline
value class Foo(val value: String)

@JvmInline
value class Width(val value: Double)

class ValidatedTest : FunSpec() {
   init {

      test("parsing strings to domain object") {
         Parser<String>().parse("input").map { Foo("input") } shouldBe Foo("input").valid()
      }

      test("parsing non blank strings to domain object") {
         Parser<String>()
            .notBlank { "cannot be blank" }
            .map { Foo("input") }
            .parse("    ") shouldBe Validated.invalidNel("cannot be blank")
      }

      test("parser should support default") {
         val p = Parser<String>()
            .mapIfNotNull { Foo(it) }
            .withDefault { Foo("foo") }
         val result: Foo = p.parse("foo").getOrThrow()
      }

      test("parser should support default on nullable inputs") {
         val p = Parser<String?>()
            .mapIfNotNull { Foo(it) }
            .withDefault { Foo("foo") }
         val result: Foo = p.parse("foo").getOrThrow()
      }

      test("parser should support booleans") {
         val p = Parser<String>().boolean { "not a boolean" }
         p.parse("foo").getErrorsOrThrow() shouldBe listOf("not a boolean")
         p.parse("true").getOrThrow() shouldBe true
         p.parse("false").getOrThrow() shouldBe false
      }

      test("parser should support longs") {
         val p = Parser<String>().long { "not a long" }
         p.parse("foo").getErrorsOrThrow() shouldBe listOf("not a long")
         p.parse("12345").getOrThrow() shouldBe 12345L
      }

      test("parser should support doubles") {
         val p = Parser<String>().double { "not a double" }.map { Width(it) }
         p.parse("foo").getErrorsOrThrow() shouldBe listOf("not a double")
         p.parse("123.45").getOrThrow() shouldBe Width(123.45)
      }

      test("parser should support doubles with nullable pass through") {
         val p = Parser<String>().double { "not a double" }.allowNulls()
         p.parse("123.45").getOrThrow() shouldBe 123.45
         p.parse(null).getOrThrow() shouldBe null
      }

      test("parser should support doubles with nullable failure message") {
         val p = Parser<String?>().notNull { "cannot be null" }.double { "not a double" }
         p.parse("123.45").getOrThrow() shouldBe 123.45
         p.parse(null).getErrorsOrThrow() shouldBe listOf("cannot be null")
      }

      test("parser should support floats") {
         val p = Parser<String>().float { "not a float" }
         p.parse("foo").getErrorsOrThrow() shouldBe listOf("not a float")
         p.parse("123.45").getOrThrow() shouldBe 123.45F
      }

      test("repeated parser") {
         val ps = Parser<String>().map { Foo(it) }.repeated()
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).valid()
      }

      test("repeated with min length") {
         val ps = Parser<String>().map { Foo(it) }.repeated(min = 2) { "Must have at least two elements" }
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).valid()
         ps.parse(listOf("a")) shouldBe "Must have at least two elements".invalid()
      }

      test("filter") {
         val p = Parser<String>().filter(String::isNotEmpty) { "boom" }.map { Foo(it) }
         p.parse("") shouldBe "boom".invalid()
         p.parse("abc") shouldBe Foo("abc").valid()
      }

      test("contains") {
         val p = Parser<String>().oneOf(listOf("a", "b")) { "boom" }.map { Foo(it) }
         p.parse("") shouldBe "boom".invalid()
         p.parse("c") shouldBe "boom".invalid()
         p.parse("a") shouldBe Foo("a").valid()
         p.parse("b") shouldBe Foo("b").valid()
      }

      test("composite parser") {
         data class UpdateRequest(val name: String, val age: String?)
         data class ValidName(val name: String)
         data class ValidAge(val age: Int)

         val nameParser: Parser<UpdateRequest, ValidName, String> = Parser<UpdateRequest>()
            .map { it.name }
            .notBlank { "Name cannot be blank" }
            .minlen(6) { "Name must have 6 characters" }
            .map { ValidName(it) }

         val ageParser: Parser<UpdateRequest, ValidAge, String> = Parser<UpdateRequest>()
            .map { it.age }
            .notNull { "Age cannot be null" }
            .int { "Age must be a number" }
            .map { ValidAge(it) }

         val parser = Parser.compose(nameParser, ageParser)
         val result = parser.parse(UpdateRequest("a", "b"))
      }
   }
}
