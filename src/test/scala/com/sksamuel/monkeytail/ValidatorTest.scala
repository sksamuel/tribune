package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import cats.implicits._
import org.scalatest.{FlatSpec, Matchers}

class ValidatorTest extends FlatSpec with Matchers {

  import ValidatorSyntax._

  case class Starship(name: String, flagship: Boolean, maxWarp: Double)
  case class Planet(name: String, system: String)

  "Validator" should "support combine via a Monoid instance" in {

    val nameValidator: Validator[Planet] = Validator[Planet]
      .validate(_.name)(_ != null)

    val systemValidator: Validator[Planet] = Validator[Planet]
      .validate(_.system)(_ != null)

    val combinedValidator = List(nameValidator, systemValidator).combineAll
    combinedValidator(Planet(null, null)) shouldBe Invalid(NonEmptyList.of(DefaultViolation("Invalid value: null", Path("name")), DefaultViolation("Invalid value: null", Path("system"))))
  }

  it should "allow classes to be tested as a whole" in {

    val starshipValidator = Validator[Starship].test { starship =>
      starship.maxWarp < 10 && starship.name != null
    }

    starshipValidator(Starship(null, true, 12)) shouldBe
      Invalid(NonEmptyList.of(DefaultViolation("Invalid value: Starship(null,true,12.0)", Path.empty)))
  }

  "SimpleValidator" should "allow classes to be tested as a whole" in {

    val starshipValidator = Validator.simple[Starship] { starship =>
      starship.maxWarp < 10 && starship.name != null
    }

    starshipValidator(Starship(null, true, 12)) shouldBe
      Invalid(NonEmptyList.of(DefaultViolation("Invalid value: Starship(null,true,12.0)", Path.empty)))
  }

  "Validator.notnull" should "Add validation rule for every field" in {
    Validator.notnull[Planet](Planet(null, null)) shouldBe
      Invalid(NonEmptyList.of(
        DefaultViolation("name should not be null", Path(List("name"))),
        DefaultViolation("system should not be null", Path(List("system")))
      ))
  }

  it should "support being used as the basis for custom validations" in {
    Validator.notnull[Starship]
      .validate(_.maxWarp)(_ < 10)
  }
}
