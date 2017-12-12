package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.Invalid
import cats.implicits._
import org.scalatest.{FlatSpec, Matchers}

class ValidatorTest extends FlatSpec with Matchers {

  case class Planet(name: String, system: String)

  "Validators" should "support combine via a Monoid instance" in {

    val nameValidator: Validator[Planet] = Validator[Planet]
      .field(_.name)(_ != null)

    val systemValidator: Validator[Planet] = Validator[Planet]
      .field(_.system)(_ != null)

    val combinedValidator = List(nameValidator, systemValidator).combineAll
    combinedValidator(Planet(null, null)) shouldBe Invalid(NonEmptyList.of(BasicViolation("name has invalid value: null"), BasicViolation("system has invalid value: null")))
  }

  it should "allow classes to be tested as a whole" in {

    val starshipValidator = Validator.simple[Starship] { starship =>
      starship.maxWarp < 10 && starship.name != null
    }

    starshipValidator(Starship(null, true, 12)) shouldBe
      Invalid(NonEmptyList.of(BasicViolation(" has invalid value: Starship(null,true,12.0)")))
  }
}
