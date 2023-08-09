package com.sksamuel.tribune.core

import arrow.core.invalidNel
import arrow.core.validNel
import com.sksamuel.tribune.core.strings.match
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.maxlen
import com.sksamuel.tribune.core.strings.minlen
import com.sksamuel.tribune.core.strings.notNullOrBlank
import com.sksamuel.tribune.core.strings.strip
import com.sksamuel.tribune.core.strings.toLowercase
import com.sksamuel.tribune.core.strings.toUppercase
import com.sksamuel.tribune.core.strings.trim
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringTest : FunSpec() {
   init {

      test("not null") {
         val p = Parser<String?>().notNull { "cannot be null" }.map { Foo(it) }
         p.parse(null) shouldBe "cannot be null".invalidNel()
      }

      test("not null or blank") {
         val p = Parser<String>().allowNulls().notNullOrBlank { "cannot be null or blank" }.map { Foo(it) }
         p.parse("") shouldBe "cannot be null or blank".invalidNel()
         p.parse("     ") shouldBe "cannot be null or blank".invalidNel()
         p.parse(null) shouldBe "cannot be null or blank".invalidNel()
      }

      test("min length") {
         val p = Parser<String>().minlen(4) { "too short" }.map { Foo(it) }
         p.parse("abc") shouldBe "too short".invalidNel()
         p.parse("abcd") shouldBe Foo("abcd").validNel()
      }

      test("max length") {
         val p = Parser<String>().maxlen(4) { "too long" }.map { Foo(it) }
         p.parse("abcde") shouldBe "too long".invalidNel()
         p.parse("abcd") shouldBe Foo("abcd").validNel()
         p.parse("abc") shouldBe Foo("abc").validNel()
      }

      test("length") {
         val p = Parser<String>().length(4) { "must be 4 chars" }.map { Foo(it) }
         p.parse("abcde") shouldBe "must be 4 chars".invalidNel()
         p.parse("abcd") shouldBe Foo("abcd").validNel()
         p.parse("abc") shouldBe "must be 4 chars".invalidNel()
      }

      test("trim") {
         val p = Parser<String>().trim().map { Foo(it) }
         p.parse(" abcd ") shouldBe Foo("abcd").validNel()
         p.parse("abc    ") shouldBe Foo("abc").validNel()
      }

      test("uppercase") {
         val p = Parser<String>().toUppercase().map { Foo(it) }
         p.parse("abcd") shouldBe Foo("ABCD").validNel()
      }

      test("lowercase") {
         val p = Parser<String>().toLowercase().map { Foo(it) }
         p.parse("ABCD") shouldBe Foo("abcd").validNel()
      }

      test("strip") {
         val p = Parser<String>().strip(charArrayOf('a', 'b'))
         p.parse("aaccbb") shouldBe "cc".validNel()
         p.parse("aa") shouldBe "".validNel()
         p.parse("ddd") shouldBe "ddd".validNel()
      }

      test("match") {
         val p = Parser<String>().match("aa.".toRegex()) { "$it is whack!" }
         p.parse("aab") shouldBe "aab".validNel()
         p.parse("aac") shouldBe "aac".validNel()
         p.parse("aa") shouldBe "aa is whack!".invalidNel()
         p.parse("baa") shouldBe "baa is whack!".invalidNel()
      }
   }
}
