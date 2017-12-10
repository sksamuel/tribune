package com.sksamuel.monkeytail

import cats.data.ValidatedNel

trait Violation {
  def message: String
}

trait Validator[T] {

  type ValidationResult = ValidatedNel[Violation, T]

  def validate(t: T): ValidationResult
}
