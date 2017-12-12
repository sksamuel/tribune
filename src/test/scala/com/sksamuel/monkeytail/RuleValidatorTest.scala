package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{FlatSpec, Matchers}

class RuleValidatorTest extends FlatSpec with Matchers {

  case class Starship(name: String, flagship: Boolean, maxWarp: Double)

  "RuleValidator" should "allow fields of any type to be validated" in {

    val starshipValidator = Validator[Starship]
      .field(_.name)(_ != null)
      .field(_.maxWarp)(_ < 10)
      .field(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 9)) shouldBe Valid(Starship("Enterprise",true,9.0))
    starshipValidator(Starship(null, true, 9)) shouldBe Invalid(NonEmptyList.of(BasicViolation("name has invalid value: null")))
    starshipValidator(Starship("Enterprise", false, 9)) shouldBe Invalid(NonEmptyList.of(BasicViolation("flagship has invalid value: false")))
    starshipValidator(Starship(null, false, 11)) shouldBe Invalid(NonEmptyList.of(BasicViolation("name has invalid value: null"), BasicViolation("maxWarp has invalid value: 11.0"), BasicViolation("flagship has invalid value: false")))
  }

  it should "support custom violations" in {

    case object MaxWarpExceededViolation extends Violation {
      override def message = "Max Warp Exceeded"
    }

    val starshipValidator = Validator[Starship]
      .field(_.name)(_ != null)
      .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
      .field(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 11)) shouldBe Invalid(NonEmptyList.of(MaxWarpExceededViolation))
  }

  it should "support violation builders" in {

    object MaxWarpExceededViolation extends ViolationBuilder[Double] {
      override def apply(name: String, value: Double): Violation = BasicViolation(s"Max warp exceeded, was $value")
    }

    val starshipValidator = Validator[Starship]
      .field(_.name)(_ != null)
      .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
      .field(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 11)) shouldBe Invalid(NonEmptyList.of(BasicViolation("Max warp exceeded, was 11.0")))

  }

  it should "supported nested validation" in {

  }

  it should "return a context path to nested fields" in {

  }
}