package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import sun.security.validator.SimpleValidator

/**
  * Instances of Validator[T] can be invoked for a T for validation
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

  // returns a rule based validator which already has a rule added for every
  // field to ensure all fields are not null
  def nonull[T <: Product]: RuleValidator[T] = new RuleValidator[T](Nil)

  // create a rule based validator for case classes
  def apply[T <: Product]: RuleValidator[T] = new RuleValidator[T](Nil)

  def simple[T](testFn: T => Boolean)(implicit builder: ViolationBuilder[T] = BasicViolationBuilder): SimpleValidator[T] = new SimpleValidator[T] {
    override def test(t: T): Option[Violation] = if (testFn(t)) None else Option(builder.apply(NoPath, t))
  }

  // allows validators to be used against sequences
  implicit def seqVal[T](implicit validator: Validator[T]): Validator[Seq[T]] = new Validator[Seq[T]] {
    override def apply(t: Seq[T]): Validated[NonEmptyList[Violation], Seq[T]] = {
      val errors = t.map(validator.apply).toList collect {
        case Invalid(errs) => errs.toList
      }
      if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
    }
  }
}