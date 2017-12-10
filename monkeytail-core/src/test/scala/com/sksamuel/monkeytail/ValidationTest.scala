package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import org.scalatest.{FlatSpec, Matchers}

class ValidationTest extends FlatSpec with Matchers {

  "Validation" should "validate" in {
    val validator: Validator[Starship] = new Validator[Starship] {
      override def validate(t: Starship): this.ValidationResult = {
        Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
      }
    }
    validator.validate(Starship("Enterprise", "1701", 12)) shouldBe Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
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