package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.match
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
         p.parse(null) shouldBe "cannot be null".leftNel()
      }

      test("not null or blank") {
         val p = Parser<String>().allowNulls().notNullOrBlank { "cannot be null or blank" }.map { Foo(it) }
         p.parse("") shouldBe "cannot be null or blank".leftNel()
         p.parse("     ") shouldBe "cannot be null or blank".leftNel()
         p.parse(null) shouldBe "cannot be null or blank".leftNel()
      }

      test("min length") {
         val p = Parser<String>().minlen(4) { "too short" }.map { Foo(it) }
         p.parse("abc") shouldBe "too short".leftNel()
         p.parse("abcd") shouldBe Foo("abcd").right()
      }

      test("min length or null") {
         val p = Parser<String?>().minlen(4) { "too short" }.mapIfNotNull { Foo(it) }
         p.parse("abc") shouldBe "too short".leftNel()
         p.parse("abcd") shouldBe Foo("abcd").right()
         p.parse(null) shouldBe Either.Right(null)
      }

      test("max length") {
         val p = Parser<String>().maxlen(4) { "too long" }.notNull { "not null" }.map { Foo(it) }
         p.parse("abcde") shouldBe "too long".leftNel()
         p.parse("abcd") shouldBe Foo("abcd").right()
         p.parse("abc") shouldBe Foo("abc").right()
      }

      test("max length on nullable string") {
         val p = Parser<String?>().maxlen(4) { "too long" }.mapIfNotNull { Foo(it) }
         p.parse("abcde") shouldBe "too long".leftNel()
         p.parse("abcd") shouldBe Foo("abcd").right()
         p.parse("abc") shouldBe Foo("abc").right()
         p.parse(null) shouldBe Either.Right(null)
      }

      test("length") {
         val p = Parser<String>().length(4) { "must be 4 chars" }.map { Foo(it) }
         p.parse("abcde") shouldBe "must be 4 chars".leftNel()
         p.parse("abcd") shouldBe Foo("abcd").right()
         p.parse("abc") shouldBe "must be 4 chars".leftNel()
      }

      test("trim") {
         val p = Parser<String>().trim().map { Foo(it) }
         p.parse(" abcd ") shouldBe Foo("abcd").right()
         p.parse("abc    ") shouldBe Foo("abc").right()
      }

      test("uppercase") {
         val p = Parser<String>().toUppercase().map { Foo(it) }
         p.parse("abcd") shouldBe Foo("ABCD").right()
      }

      test("uppercaseOrNull") {
         val p = Parser<String?>().toUppercase().mapIfNotNull { Foo(it) }
         p.parse("abcd") shouldBe Foo("ABCD").right()
         p.parse(null) shouldBe Either.Right(null)
      }

      test("lowercase") {
         val p = Parser<String>().toLowercase().map { Foo(it) }
         p.parse("ABCD") shouldBe Foo("abcd").right()
      }

      test("lowercase or null") {
         val p = Parser<String?>().toLowercase().mapIfNotNull { Foo(it) }
         p.parse("ABCD") shouldBe Foo("abcd").right()
         p.parse(null) shouldBe Either.Right(null)
      }

      test("strip") {
         val p = Parser<String>().strip(charArrayOf('a', 'b'))
         p.parse("aaccbb") shouldBe "cc".right()
         p.parse("aa") shouldBe "".right()
         p.parse("ddd") shouldBe "ddd".right()
      }

      test("match") {
         val p = Parser<String>().match("aa.".toRegex()) { "$it is whack!" }
         p.parse("aab") shouldBe "aab".right()
         p.parse("aac") shouldBe "aac".right()
         p.parse("aa") shouldBe "aa is whack!".leftNel()
         p.parse("baa") shouldBe "baa is whack!".leftNel()
      }
   }
}
