package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

import scala.language.experimental.macros

// an implementation of Validator that allows rules to be added based on type T
// when validate is invoked, all the rules are executed against an instance of T
class RuleValidator[T](val rules: List[Rule[T]]) extends Validator[T] {

  def apply(t: T): Validated[NonEmptyList[Violation], T] = {
    val errors = rules.map(_.apply(t)) collect {
      case Invalid(errs) => errs.toList
    }
    if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
  }

  // prepares a field for a rule
  def field[U](extractor: T => U): FieldContext[T, U] = macro Macros.fieldContext[T, U]

  // prepares an iterable field for a rule
  def forall[U](extractor: T => Seq[U]): SeqContext[T, U] = macro Macros.seqContext[T, Seq[U]]

  // allows us to test the entire object
  def test(test: T => Boolean)(implicit builder: ViolationBuilder[T] = DefaultViolationBuilder): RuleValidator[T] = {
    val rule = new Rule[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = if (test(t)) Valid(t) else Invalid(NonEmptyList.of(builder(Path.empty, t)))
    }
    new RuleValidator[T](rules :+ rule)
  }

  // adds a rule that uses an existing validator for the extracted field
  def valid[U](extractor: T => U)(implicit validator: Validator[U]): RuleValidator[T] = {
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

case class SeqContext[T, U](extractor: T => Seq[U], validator: RuleValidator[T], path: Path) {

  def apply(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = {
    val rule = new Rule[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
        val us = extractor(t).toList
        val validations = us.zipWithIndex.map { case (u, index) =>
          if (test(u)) Valid(t) else {
            val violation = builder(path.withIndex(index), u)
            Invalid(NonEmptyList.of(violation))
          }
        }
        Validator.reduce(t, validations)
      }
    }
    new RuleValidator[T](validator.rules :+ rule)
  }
}

case class FieldContext[T, U](extractor: T => U, validator: RuleValidator[T], path: Path) {

  def apply(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): RuleValidator[T] = {
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