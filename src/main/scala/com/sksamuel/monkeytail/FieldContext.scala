package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

case class FieldContext[T, U](extractor: T => U, // function to retrieve the field we want to test
                              parent: Validator[T], // the current validator we'll be combining with
                              path: Path // the path to the field we're evaluating
                             ) {

  def apply(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): Validator[T] = {
    val validator = new Validator[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
        val value = extractor(t)
        if (test(value)) Valid(t) else {
          val violation = builder(path, value)
          Invalid(NonEmptyList.of(violation))
        }
      }
    }
    Validator.monoid[T].combine(parent, validator)
  }
}
