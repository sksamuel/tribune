package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{FlatSpec, Matchers}

class ValidatorSyntaxTest extends FlatSpec with Matchers {

  import ValidatorSyntax._

  case class Starship(name: String, flagship: Boolean, maxWarp: Double)

  "ValidatorSyntax" should "allow fields of any type to be validated" in {

    val starshipValidator = Validator[Starship]
      .validate(_.name)(_ != null)
      .validate(_.maxWarp)(_ < 10)
      .validate(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 9)) shouldBe Valid(Starship("Enterprise", true, 9.0))
    starshipValidator(Starship(null, true, 9)) shouldBe Invalid(NonEmptyList.of(DefaultViolation("Invalid value: null", Path("name"))))
    starshipValidator(Starship("Enterprise", false, 9)) shouldBe Invalid(NonEmptyList.of(DefaultViolation("Invalid value: false", Path("flagship"))))
    starshipValidator(Starship(null, false, 11)) shouldBe
      Invalid(NonEmptyList.of(
        DefaultViolation("Invalid value: null", Path("name")),
        DefaultViolation("Invalid value: 11.0", Path("maxWarp")),
        DefaultViolation("Invalid value: false", Path("flagship"))
      ))
  }

  it should "support custom violations" in {

    case object MaxWarpExceededViolation extends Violation

    val starshipValidator = Validator[Starship]
      .validate(_.name)(_ != null)
      .validate(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
      .validate(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 11)) shouldBe Invalid(NonEmptyList.of(MaxWarpExceededViolation))
  }

  it should "support violation builders" in {

    case class MaxWarpViolation(message: String) extends Violation

    object MaxWarpExceededViolation extends ViolationBuilder[Double] {
      override def apply(path: Path, value: Double) = MaxWarpViolation(s"Max warp exceeded, was $value")
    }

    val starshipValidator = Validator[Starship]
      .validate(_.name)(_ != null)
      .validate(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
      .validate(_.flagship)(_ == true)

    starshipValidator(Starship("Enterprise", true, 11)) shouldBe Invalid(NonEmptyList.of(MaxWarpViolation("Max warp exceeded, was 11.0")))
  }

  it should "support validation of sequences by delegation" in {

    case class Foo(name: String)
    case class Wibble(foos: Seq[Foo])

    implicit val fooValidator: Validator[Foo] = Validator[Foo]
      .validate(_.name)(_ != null)

    val validator = Validator[Wibble]
      .forall(_.foos, fooValidator)

    validator(Wibble(Seq(Foo(null), Foo("a"), Foo(null)))) shouldBe
      Invalid(NonEmptyList.of(
        DefaultViolation("Invalid value: null", Path("name")),
        DefaultViolation("Invalid value: null", Path("name"))
      ))
  }

  it should "support validation of sequences using forall" in {

    case class Foo(name: String)
    case class Wibble(foos: Seq[Foo])

    val validator = Validator[Wibble]
      .forall(_.foos)(_.name != null)

    validator(Wibble(Seq(Foo(null), Foo("a"), Foo(null)))) shouldBe
      Invalid(NonEmptyList.of(
        DefaultViolation("Invalid value: Foo(null)", Path.empty),
        DefaultViolation("Invalid value: Foo(null)", Path.empty)
      ))
  }

  it should "supported nested validation with implicit lookup" in {

    case class Foo(name: String)
    case class Wibble(foo: Foo)

    implicit val fooValidator: Validator[Foo] = Validator[Foo]
      .validate(_.name)(_ != null)

    val validator = Validator[Wibble]
      .valid(_.foo)

    validator(Wibble(Foo(null))) shouldBe
      Invalid(NonEmptyList.of(DefaultViolation("Invalid value: null", Path("name"))))
  }

  it should "return a context path to nested fields" in {

    case class Foo(name: String)
    case class Wibble(foo: Foo)

    val validator = Validator[Wibble]
      .validate(_.foo.name)(_ != null)

    validator(Wibble(Foo(null))) shouldBe
      Invalid(NonEmptyList.of(DefaultViolation("Invalid value: null", Path("foo", "name"))))
  }

  it should "support sanitizing of fields" in {

    val validator = Validator[Starship]
      .sanitize(_.name)(_.toLowerCase)
      .sanitize(_.maxWarp)(_ - 1)

    validator(Starship("ENTERPRISE", true, 9.6)) shouldBe Valid(Starship("enterprise", true, 8.6))
  }
}