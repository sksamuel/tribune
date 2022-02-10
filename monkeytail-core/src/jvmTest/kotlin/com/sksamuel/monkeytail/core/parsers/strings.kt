package com.sksamuel.monkeytail.core.parsers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringTest : FunSpec() {
   init {

      test("not null") {
         val p = Parser<String?>().notNull { "cannot be null" }.map { Foo(it) }
         p.parse(null) shouldBe "cannot be null".invalid()
      }

      test("not null or blank") {
         val p = Parser<String>().allowNulls().notNullOrBlank { "cannot be null or blank" }.map { Foo(it) }
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

      test("strip") {
         val p = Parser<String>().strip(charArrayOf('a', 'b'))
         p.parse("aaccbb") shouldBe "cc".valid()
         p.parse("aa") shouldBe "".valid()
         p.parse("ddd") shouldBe "ddd".valid()
      }

      test("match") {
         val p = Parser<String>().match("aa.".toRegex()) { "$it is whack!" }
         p.parse("aab") shouldBe "aab".valid()
         p.parse("aac") shouldBe "aac".valid()
         p.parse("aa") shouldBe "aa is whack!".invalid()
         p.parse("baa") shouldBe "baa is whack!".invalid()
      }
   }
}
