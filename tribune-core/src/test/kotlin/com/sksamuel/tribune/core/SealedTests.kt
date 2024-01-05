package com.sksamuel.tribune.core

import arrow.core.*
import com.sksamuel.tribune.core.strings.match
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

sealed interface ProductCodeError {
   data object NotWidgetCode : ProductCodeError
   data object NotGizmoCode : ProductCodeError
}

sealed interface ProductCode {
   val value: String

   @JvmInline
   value class GizmoCode private constructor(override val value: String) : ProductCode {
      companion object {
         private const val GIZMO_PATTERN = "\\AG\\d{3}\\z"
         operator fun invoke(s: String): EitherNel<ProductCodeError.NotGizmoCode, GizmoCode> = parser.parse(s)

         val parser: Parser<String, GizmoCode, ProductCodeError.NotGizmoCode> by lazy {
            Parser<String>()
               .match(Regex(GIZMO_PATTERN)) { ProductCodeError.NotGizmoCode }
               .map { GizmoCode(it) }
         }
      }
   }

   @JvmInline
   value class WidgetCode private constructor(override val value: String) : ProductCode {
      companion object {
         private const val WIDGET_PATTERN = "\\AW\\d{4}\\z"
         operator fun invoke(s: String): EitherNel<ProductCodeError.NotWidgetCode, WidgetCode> = parser.parse(s)

         val parser: Parser<String, WidgetCode, ProductCodeError.NotWidgetCode> by lazy {
            Parser<String>()
               .match(Regex(WIDGET_PATTERN)) { ProductCodeError.NotWidgetCode }
               .map { WidgetCode(it) }
         }
      }
   }

   companion object {
      operator fun invoke(s: String): EitherNel<ProductCodeError, ProductCode> = parser.parse(s)

      val parser: Parser<String, ProductCode, ProductCodeError> = GizmoCode.parser.orElse(WidgetCode.parser)
   }


}

class SealedTests : FunSpec() {
   init {
      test("GizmoCode success") {
         val s = "G123"
         ProductCode.GizmoCode(s).map { it.value } shouldBe s.right()
      }

      test("GizmoCode failure") {
         ProductCode.GizmoCode("12342") shouldBe ProductCodeError.NotGizmoCode.nel().left()
      }

      test("WidgetCode success") {
         val s = "W1234"
         ProductCode.WidgetCode(s).map { it.value } shouldBe s.right()
      }

      test("WidgetCode failure") {
         ProductCode.WidgetCode("12342") shouldBe ProductCodeError.NotWidgetCode.nel().left()
      }

      test("ProductCode success") {
         ProductCode("W1234") shouldBe ProductCode.WidgetCode("W1234")
         ProductCode("G123") shouldBe ProductCode.GizmoCode("G123")
      }

      test("ProductCode failure") {
         ProductCode("1234") shouldBe nonEmptyListOf(ProductCodeError.NotGizmoCode, ProductCodeError.NotWidgetCode).left()
      }
   }
}