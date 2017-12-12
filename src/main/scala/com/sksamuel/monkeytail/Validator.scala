package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

trait Validator[T] {
  def apply(t: T): Validated[NonEmptyList[Violation], T]
}

object Validator {

  def apply[T]: RuleValidator[T] = new RuleValidator[T](Nil)

  implicit def seqVal[T](implicit validator: Validator[T]): Validator[Seq[T]] = new Validator[Seq[T]] {
    override def apply(t: Seq[T]): Validated[NonEmptyList[Violation], Seq[T]] = {
      val errors = t.map(validator.apply).toList collect {
        case Invalid(errs) => errs.toList
      }
      if (errors.nonEmpty) Invalid(NonEmptyList.fromListUnsafe(errors.flatten)) else Valid(t)
    }
  }
}