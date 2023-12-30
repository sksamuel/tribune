package com.sksamuel.tribune.core

import com.sksamuel.tribune.core.ints.min
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ComposeTest : FunSpec() {
   init {
      test("compose happy path") {

         val parser = Parser.compose(
            weightParser.contramap<Input> { it.weight },
            heightParser.contramap { it.height },
            ageParser.contramap { it.age }
         ) { a, b, c -> Output(a, b, c) }

         val input = Input(1.0, 2.0, 21)

         parser.parse(input).getOrNull() shouldBe Output(
            weight = ParsedWeight(1.0),
            height = ParsedHeight(2.0),
            age = ParsedAge(21),
         )
      }

      test("compose should accumulate errors") {

         val parser = Parser.compose(
            weightParser.contramap<Input> { it.weight },
            heightParser.contramap { it.height },
            ageParser.contramap { it.age }
         ) { a, b, c -> Output(a, b, c) }

         val input = Input(null, 2.0, 17)

         parser.parse(input).leftOrNull() shouldBe listOf("Weight not be null", "Min age is 18")
      }
   }
}

val q = Parsers.nullableString.nullIf { it.length > 3 }

val weightParser = Parsers.nullableDouble
   .nullIf { it <= 0.0 }
   .mapIfNotNull { ParsedWeight(it) }
   .notNull { "Weight not be null" }

val heightParser = Parsers.nullableDouble.nullIf { it <= 0.0 }.mapIfNotNull { ParsedHeight(it) }

val ageParser = Parsers.nullableInt
   .min(18) { "Min age is 18" }
   .mapIfNotNull { ParsedAge(it) }

data class ParsedHeight(val value: Double)
data class ParsedWeight(val value: Double)
data class ParsedAge(val value: Int)

data class Input(
   val weight: Double?,
   val height: Double?,
   val age: Int?
)

data class Output(
   val weight: ParsedWeight?,
   val height: ParsedHeight?,
   val age: ParsedAge?,
)
