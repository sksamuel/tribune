package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

import scala.language.experimental.macros

// an implementation of Validator that allows rules to be added based on the fields of T
// when validate is invoked, all the rules are executed against the fields of T
class RuleValidator[T](val rules: List[Rule[T]]) extends Validator[T] {

  def apply(t: T): Validated[NonEmptyList[Violation], T] = {
    val errors = rules.map(_.apply(t)) collect {
      case Invalid(errs) => errs.toList
    }
    if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
  }

  // prepares a field for a rule to be added
  def field[U](extractor: T => U): FieldContext[T, U] = macro Macros.fieldContext[T, U]

  def validate[U](extractor: T => U)(implicit validator: Validator[U]): RuleValidator[T] = {
    val rule = new Rule[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = validator(extractor(t)) match {
        case Invalid(errors) => Invalid(errors)
        case Valid(_) => Valid(t)
      }
    }
    new RuleValidator[T](rules :+ rule)
  }
}

trait Rule[T] extends (T => Validated[NonEmptyList[Violation], T])

case class FieldContext[T, U](extractor: T => U, validator: RuleValidator[T], path: Path) {

  def apply(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = validate(test)

  def validate(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = {
    val rule = new Rule[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
        val value = extractor(t)
        if (test(value)) Valid(t) else {
          val violation = builder(path, value)
          Invalid(NonEmptyList.of(violation))
        }
      }
    }
    new RuleValidator[T](validator.rules :+ rule)
  }
}