package com.sksamuel.monkeytail

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}

import scala.language.experimental.macros

object ValidatorSyntax {

  // enables the use of the magic macro methods on a validator
  implicit class ValidatorOps[T <: Product](val wrapped: Validator[T]) {

    // prepares a field to be used as the basis for a validator
    @deprecated("use validate(T => U)", "0.91.0")
    def field[U](extractor: T => U): FieldContext[T, U] = macro Macros.fieldContext[T, U]
    def validate[U](extractor: T => U): FieldContext[T, U] = macro Macros.fieldContext[T, U]

    // prepares an iterable field to be used as a validator
    def forall[U](extractor: T => Seq[U]): SeqContext[T, U] = macro Macros.seqContext[T, U]

    def forall[U](extractor: T => Seq[U], validator: Validator[U]): Validator[T] = {
      val delegate = Validator.seq[T, U](extractor, validator)
      Validator.monoid[T].combine(wrapped, delegate)
    }

    // prepares a field to be used for sanitization
    def sanitize[U](extractor: T => U): SanitizeContext[T, U] = macro Macros.sanitizeContext[T, U]

    // uses an existing validator for the extracted field
    def valid[U](extractor: T => U)(implicit validator: Validator[U]): Validator[T] = {
      val delegate = new Validator[T] {
        override def apply(t: T): Validated[NonEmptyList[Violation], T] = validator(extractor(t)) match {
          case Invalid(errors) => Invalid(errors)
          case Valid(_) => Valid(t)
        }
      }
      Validator.monoid[T].combine(wrapped, delegate)
    }

    // allows us to test the entire object
    def test(test: T => Boolean)(implicit builder: ViolationBuilder[T] = DefaultViolationBuilder): Validator[T] = {
      val validator = new Validator[T] {
        override def apply(t: T): Validated[NonEmptyList[Violation], T] =
          if (test(t)) Valid(t) else Invalid(NonEmptyList.of(builder(Path.empty, t)))
      }
      Validator.monoid[T].combine(wrapped, validator)
    }
  }
}