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

class DefaultValidator[T](test: T => Boolean, builder: ViolationBuilder[T]) extends Validator[T] {
  override def apply(t: T): Validated[NonEmptyList[Violation], T] =
    if (test(t)) Valid(t) else Invalid(NonEmptyList.of(builder(Path.empty, t)))
}

trait SimpleValidator[T] extends Validator[T] {

  def test(t: T): Option[Violation]

  def apply(t: T): Validated[NonEmptyList[Violation], T] = test(t) match {
    case Some(violation) => Invalid(NonEmptyList.of(violation))
    case None => Valid(t)
  }
}

object Validator {

  // the empty/identity validator that performs no validation at all
  def apply[T]: Validator[T] = new Validator[T] {
    override def apply(t: T) = Valid(t)
  }

  implicit def monoid[T]: Monoid[Validator[T]] = new Monoid[Validator[T]] {
    override def empty: Validator[T] = Validator.apply[T]
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

  // returns a Validator for T that handles nested sequences by delegation to a validator for the element type
  def seq[T, U](extractor: T => Seq[U], validator: Validator[U])
               (implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): Validator[T] = new Validator[T] {
    override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
      // extract the sequence
      val validations = extractor(t).toList.map(validator.apply)
      val errors = validations.collect {
        case Invalid(errs) => errs.toList
      }
      if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
    }
  }

  def simple[T](testFn: T => Boolean)(implicit builder: ViolationBuilder[T] = DefaultViolationBuilder): SimpleValidator[T] = new SimpleValidator[T] {
    override def test(t: T): Option[Violation] = if (testFn(t)) None else Option(builder.apply(Path.empty, t))
  }
}