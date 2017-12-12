package com.sksamuel.monkeytail

import scala.language.implicitConversions

trait Violation {
  def message: String
}

object Violation {
  implicit def violation2builder[Any](violation: Violation): ViolationBuilder[Any] = new ViolationBuilder[Any] {
    override def apply(name: String, value: Any): Violation = violation
  }
}

trait ViolationBuilder[-U] {
  def apply(name: String, value: U): Violation
}

object DefaultViolationBuilder extends ViolationBuilder[Any] {
  override def apply(name: String, value: Any): Violation = BasicViolation(s"$name has invalid value: $value")
}

case class BasicViolation(message: String) extends Violation