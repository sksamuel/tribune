package com.sksamuel.monkeytail

import cats.data.{NonEmptyList, Validated}

case class SanitizeContext[T, U](extractor: T => U, // function to retrieve the field we want to sanitize
                                 parent: Validator[T], // the current validator we will wrap with the sanitized value
                                 copyFn: (T, U) => T // a function to create a copy of T with the cleaned value applied
                                ) {

  def apply[V <: U](cleanFn: U => V)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): Validator[T] = {
    new Validator[T] {
      override def apply(t: T): Validated[NonEmptyList[Violation], T] = {
        val u = extractor(t)
        val cleanU = cleanFn(u)
        val cleanT = copyFn(t, cleanU)
        parent(cleanT)
      }
    }
  }
}
