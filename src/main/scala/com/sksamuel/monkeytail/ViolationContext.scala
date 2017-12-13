package com.sksamuel.monkeytail

import scala.language.implicitConversions

// gives a strong typing to the error
trait Violation

case class SimpleViolation(message: String) extends Violation

// the violation type used by default when no custom violation is specified
// includes a generated error message and the path to the field that caused the error
case class DefaultViolation(message: String, path: Path) extends Violation

object ViolationBuilder {
  implicit def violation2builder[Any](violation: Violation): ViolationBuilder[Any] = new ViolationBuilder[Any] {
    override def apply(path: Path, value: Any): Violation = violation
  }
}

trait ViolationBuilder[-U] {
  def apply(path: Path, value: U): Violation
}

object DefaultViolationBuilder extends ViolationBuilder[Any] {
  override def apply(path: Path, value: Any): Violation = DefaultViolation(s"Invalid value: $value", path)
}