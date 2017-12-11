package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import org.scalatest.{FlatSpec, Matchers}

trait Must

class ValidationTest extends FlatSpec with Matchers {

  implicit class RichField[T](field: T) {
    def must(matcher: Must): Validator[T] = macros.Validate(field)
  }

  object beNull extends Must

  def validator[T](fn: T => Unit): Unit = {

  }

  "Validation Macros" should "build validator" in {
    val v1: Validator[Starship] = new Validator[Starship] {
      override def validate(t: Starship): this.ValidationResult = {
        Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
      }
    }

    val v2 = validator[Starship] { starship =>
      starship.maxWarp must beNull
    }

    v1.validate(Starship("Enterprise", "1701", 12)) shouldBe Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
  }
}

case object MaxWarpExceeded extends Violation {
  override def message = "Max warp exceeded"
}

case object InvalidDesignation extends Violation {
  override def message = "Invalid designation"
}

case object NullName extends Violation {
  override def message = "Starship name cannot be null"
}

case class Starship(name: String, designation: String, maxWarp: Double)