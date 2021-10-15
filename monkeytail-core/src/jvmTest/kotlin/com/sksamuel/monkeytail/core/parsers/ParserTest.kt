package com.sksamuel.monkeytail.core.parsers

import com.sksamuel.monkeytail.core.validation.Validated
import com.sksamuel.monkeytail.core.validation.invalid
import com.sksamuel.monkeytail.core.validation.valid
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@JvmInline
value class Foo(val value: String)

@JvmInline
value class Width(val value: Double)

class ValidatedTest : FunSpec() {
   init {

      test("parsing strings to domain object") {
         Parser.string.parse("input").map { Foo("input") } shouldBe Foo("input").valid()
      }

      test("parsing non blank strings to domain object") {
         Parser.string
            .notBlank { "cannot be blank" }
            .map { Foo("input") }
            .parse("    ") shouldBe Validated.invalid("cannot be blank")
      }

      test("parser should support default") {
         val p = Parser<String>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getUnsafe()
      }

      test("parser should support default on nullable inputs") {
         val p = Parser<String?>()
            .mapIfNotNull { Foo(it) }
            .default { Foo("foo") }
         val result: Foo = p.parse("foo").getUnsafe()
      }

      test("parser should support booleans") {
         val p = Parser<String>().boolean { "not a boolean" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a boolean")
         p.parse("true").getUnsafe() shouldBe true
         p.parse("false").getUnsafe() shouldBe false
      }

      test("parser should support ints") {
         val p = Parser<String>().long { "not an int" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not an int")
         p.parse("12345").getUnsafe() shouldBe 12345
      }

      test("parser should support ints with nullable pass through") {
         val p = Parser<String>().long { "not an int" }.nullable()
         p.parse("12345").getUnsafe() shouldBe 12345
         p.parse(null).getUnsafe() shouldBe null
      }

      test("parser should support ints with nullable failure message") {
         val p = Parser<String>().long { "not an int" }.notNull { "cannot be null" }
         p.parse("12345").getUnsafe() shouldBe 12345
         p.parse(null).getErrorsUnsafe() shouldBe listOf("cannot be null")
      }

      test("parser should support longs") {
         val p = Parser<String>().long { "not a long" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a long")
         p.parse("12345").getUnsafe() shouldBe 12345L
      }

      test("parser should support doubles") {
         val p = Parser<String>().double { "not a double" }.map { Width(it) }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a double")
         p.parse("123.45").getUnsafe() shouldBe Width(123.45)
      }

      test("parser should support doubles with nullable pass through") {
         val p = Parser.string.double { "not a double" }.nullable()
         p.parse("123.45").getUnsafe() shouldBe 123.45
         p.parse(null).getUnsafe() shouldBe null
      }

      test("parser should support doubles with nullable failure message") {
         val p = Parser.string.double { "not a double" }.notNull { "cannot be null" }
         p.parse("123.45").getUnsafe() shouldBe 123.45
         p.parse(null).getErrorsUnsafe() shouldBe listOf("cannot be null")
      }

      test("parser should support floats") {
         val p = Parser.string.float { "not a float" }
         p.parse("foo").getErrorsUnsafe() shouldBe listOf("not a float")
         p.parse("123.45").getUnsafe() shouldBe 123.45F
      }

      test("repeated parser") {
         val ps = Parser.string.map { Foo(it) }.repeated()
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).valid()
      }

      test("repeated with min length") {
         val ps = Parser.string.map { Foo(it) }.repeated(min = 2) { "Must have at least two elements" }
         ps.parse(listOf("a", "b")) shouldBe listOf(Foo("a"), Foo("b")).valid()
         ps.parse(listOf("a")) shouldBe "Must have at least two elements".invalid()
      }

      test("not null") {
         val p = Parser<String>().map { Foo(it) }.notNull { "cannot be null" }
         p.parse(null) shouldBe "cannot be null".invalid()
      }

      test("not null or blank") {
         val p = Parser.string.notNullOrBlank { "cannot be null or blank" }.map { Foo(it) }
         p.parse("") shouldBe "cannot be null or blank".invalid()
         p.parse("     ") shouldBe "cannot be null or blank".invalid()
         p.parse(null) shouldBe "cannot be null or blank".invalid()
      }

      test("min length") {
         val p = Parser<String>().minlen(4) { "too short" }.map { Foo(it) }
         p.parse("abc") shouldBe "too short".invalid()
         p.parse("abcd") shouldBe Foo("abcd").valid()
      }

      test("max length") {
         val p = Parser<String>().maxlen(4) { "too long" }.map { Foo(it) }
         p.parse("abcde") shouldBe "too long".invalid()
         p.parse("abcd") shouldBe Foo("abcd").valid()
         p.parse("abc") shouldBe Foo("abc").valid()
      }

      test("length") {
         val p = Parser<String>().length(4) { "must be 4 chars" }.map { Foo(it) }
         p.parse("abcde") shouldBe "must be 4 chars".invalid()
         p.parse("abcd") shouldBe Foo("abcd").valid()
         p.parse("abc") shouldBe "must be 4 chars".invalid()
      }

      test("trim") {
         val p = Parser<String>().trim().map { Foo(it) }
         p.parse(" abcd ") shouldBe Foo("abcd").valid()
         p.parse("abc    ") shouldBe Foo("abc").valid()
      }

      test("uppercase") {
         val p = Parser<String>().uppercase().map { Foo(it) }
         p.parse("abcd") shouldBe Foo("ABCD").valid()
      }

      test("lowercase") {
         val p = Parser<String>().lowercase().map { Foo(it) }
         p.parse("ABCD") shouldBe Foo("abcd").valid()
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

      test("non neg") {
         val p = Parser<String>().int { "must be int" }.nonneg { "must be >= 0" }
         p.parse("-1") shouldBe "must be >= 0".invalid()
         p.parse("0") shouldBe 0.valid()
         p.parse("1") shouldBe 1.valid()
      }

      test("positive") {
         val p = Parser<String>().int { "must be int" }.positive { "must be > 0" }
         p.parse("0") shouldBe "must be > 0".invalid()
         p.parse("-1") shouldBe "must be > 0".invalid()
         p.parse("1") shouldBe 1.valid()
      }

      test("negative") {
         val p = Parser<String>().int { "must be int" }.negative { "must be < 0" }
         p.parse("0") shouldBe "must be < 0".invalid()
         p.parse("-1") shouldBe (-1).valid()
         p.parse("1") shouldBe "must be < 0".invalid()
      }
   }
}
