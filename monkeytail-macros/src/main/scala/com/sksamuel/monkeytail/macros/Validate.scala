package com.sksamuel.monkeytail
package macros

import scala.language.experimental.macros

object Validate {

  implicit def apply[T]: Validator[T] = macro applyImpl[T]

  def applyImpl[T: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context): c.Expr[Validator[T]] = {
    import c.universe._
    val tpe = weakTypeTag[T].tpe

    c.Expr[Validator[T]](
      q"""
        {
          new com.sksamuel.monkeytail.Validator[$tpe] {
            override def apply(t: $tpe): com.sksamuel.monkeytail.ValidationResult = {
              new com.sksamuel.monkeytail.ValidationResult(Nil)
            }
          }
        }
      """)
  }
}