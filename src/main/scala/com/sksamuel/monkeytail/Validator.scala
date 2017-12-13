package com.sksamuel.monkeytail

import cats.Monoid
import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

/**
  * Instances of Validator[T] can be invoked for a T
  * which will return a cats.Validated
  */
trait Validator[T] {
  def apply(t: T): Validated[NonEmptyList[Violation], T]
}

trait SimpleValidator[T] extends Validator[T] {

  def test(t: T): Option[Violation]

  def apply(t: T): Validated[NonEmptyList[Violation], T] = test(t) match {
    case Some(violation) => Invalid(NonEmptyList.of(violation))
    case None => Valid(t)
  }
}

object Validator {

  def identity[T]: Validator[T] = new Validator[T] {
    override def apply(t: T) = Valid(t)
  }

  implicit def monoid[T]: Monoid[Validator[T]] = new Monoid[Validator[T]] {
    override def empty: Validator[T] = identity[T]
    override def combine(val1: Validator[T], val2: Validator[T]): Validator[T] = new Validator[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = reduce(t, List(val1(t), val2(t)))
    }
  }

  def reduce[T](t: T, validations: List[Validated[NonEmptyList[Violation], T]]): Validated[NonEmptyList[Violation], T] = {
    val errors = validations collect {
      case Invalid(errs) => errs.toList
    }
    if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
  }

  // returns a rule based validator which already has a rule added for every
  // field to ensure all fields are not null
  def nonull[T <: Product]: RuleValidator[T] = new RuleValidator[T](Nil)

  // create a rule based validator for case classes
  def apply[T <: Product]: RuleValidator[T] = new RuleValidator[T](Nil)

  def simple[T](testFn: T => Boolean)(implicit builder: ViolationBuilder[T] = DefaultViolationBuilder): SimpleValidator[T] = new SimpleValidator[T] {
    override def test(t: T): Option[Violation] = if (testFn(t)) None else Option(builder.apply(Path.empty, t))
  }
}