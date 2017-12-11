package com.sksamuel.monkeytail
package macros

import scala.language.experimental.macros

object Validate {

  implicit def apply[T](field: T): Validator[T] = macro applyImpl[T]

  def applyImpl[T: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(field: c.Expr[T]): c.Expr[Validator[T]] = {
    import c.universe._
    val tpe = weakTypeTag[T].tpe
    println(showRaw(c))

    c.Expr[Validator[T]](
      q"""
        {
          new com.sksamuel.monkeytail.Validator[$tpe] {
            override def validate(t: $tpe): this.ValidationResult = {
              sys.error($field.toString)
              cats.data.Validated.Valid[$tpe](t)
            }
          }
        }
      """)
  }
}