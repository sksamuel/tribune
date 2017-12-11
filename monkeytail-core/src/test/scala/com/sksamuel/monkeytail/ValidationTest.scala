package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import com.sksamuel.monkeytail.macros.ValidatorBuilder
import org.scalatest.{FlatSpec, Matchers}

class MacroValidatorBuilder[T] extends ValidatorBuilder[T] {
  override def validate[U](field: T => U): ValidatorBuilder[T] = macros.Macros[T, U](field)
}

class ValidationTest extends FlatSpec with Matchers {

  "Validation Macros" should "build validator" in {
    val v1: Validator[Starship] = new Validator[Starship] {
      override def validate(t: Starship): this.ValidationResult = {
        Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
      }
    }

    val enterprise = Starship("Enterprise", "1701", 12)

    val v2 = new MacroValidatorBuilder[Starship]
      .validate(_.maxWarp)
      .validate(_.name)
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