package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{FlatSpec, Matchers}

trait Must

class ValidationTest extends FlatSpec with Matchers {

  implicit class RichField[T](field: T) {
    def must(matcher: Must): Unit = {
      println(s"Must $field")
      val validator = macros.Validate(field)
      validator.validate(field)
    }
  }

  object beNull extends Must

  def validator[T](fn: T => Unit): Validator[T] = new Validator[T] {
    override def validate(t: T) = {
      Valid(t)
    }
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

    val enterprise = Starship("Enterprise", "1701", 12)
    v1.validate(enterprise) shouldBe Invalid(NonEmptyList.of(MaxWarpExceeded, InvalidDesignation))
    v2.validate(enterprise)
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