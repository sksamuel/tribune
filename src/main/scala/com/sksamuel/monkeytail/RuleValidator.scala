package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

import scala.language.experimental.macros

class RuleValidator[T](val rules: List[Rule[T]]) extends Validator[T] {

  def apply(t: T): Validated[NonEmptyList[Violation], T] = {
    val errors = rules.map(_.apply(t)) collect {
      case Invalid(errs) => errs.toList
    }
    if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
  }

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

case class FieldContext[T, U](field: String, extractor: T => U, validator: RuleValidator[T]) {

  def apply(test: U => Boolean)(implicit violationFn: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = validate(test)

  def validate(test: U => Boolean)(implicit violationFn: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = {
    val rule = new Rule[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
        val value = extractor(t)
        if (test(value)) Valid(t) else {
          val violation = violationFn(field, value)
          Invalid(NonEmptyList.of(violation))
        }
      }
    }
    new RuleValidator[T](validator.rules :+ rule)
  }
}