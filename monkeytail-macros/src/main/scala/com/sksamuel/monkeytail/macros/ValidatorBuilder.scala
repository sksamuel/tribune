package com.sksamuel.monkeytail
package macros

import scala.language.experimental.macros

trait ValidatorBuilder[T] {
  def validate[U](field: T => U): ValidatorBuilder[T]
}

object Macros {

  def apply[T, U](field: T => U): ValidatorBuilder[T] = macro applyImpl[T, U]

  def applyImpl[T: c.WeakTypeTag, U](c: scala.reflect.macros.whitebox.Context)
                                    (field: c.Expr[T => U]): c.Expr[ValidatorBuilder[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    //    c.Expr[MacroValidatorBuilder[T]](
    //      q"""
    //        {
    //          new com.sksamuel.monkeytail.Validator[$tpe] {
    //            override def validate(t: $tpe): this.ValidationResult = {
    //              sys.error($field.toString)
    //              cats.data.Validated.Valid[$tpe](t)
    //            }
    //          }
    //        }
    //      """)

    c.Expr[ValidatorBuilder[T]](
      q"""new com.sksamuel.monkeytail.macros.ValidatorBuilder[$tpe] {
            def validate[U](field: $tpe => U): ValidatorBuilder[$tpe] = new ValidatorBuilder[$tpe] {
              com.sksamuel.monkeytail.macros.Macros.apply[$tpe, U](field)
            }
         }""")
  }
}

