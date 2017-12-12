package com.sksamuel.monkeytail

import scala.language.implicitConversions

trait Violation {
  def message: String
}

object Violation {
  implicit def violation2builder[Any](violation: Violation): ViolationBuilder[Any] = new ViolationBuilder[Any] {
    override def apply(path: Path, value: Any): Violation = violation
  }
}

trait ViolationBuilder[-U] {
  def apply(path: Path, value: U): Violation
}

object DefaultViolationBuilder extends ViolationBuilder[Any] {
  override def apply(path: Path, value: Any): Violation = BasicViolation(s"${path.value} has invalid value: $value")
}

case class BasicViolation(message: String) extends Violation