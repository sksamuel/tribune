package com.sksamuel.monkeytail

import scala.language.experimental.macros
import scala.language.implicitConversions

object Macros {

  def fieldContext[T, U](c: scala.reflect.macros.whitebox.Context)
                        (extractor: c.Expr[T => U]): c.universe.Expr[FieldContext[T, U]] = {
    import c.universe._
    println(c.internal)
    println(c.prefix)
    extractor.tree match {
      case Function(params, Select(q, name)) =>
        println("params=" + params)
        println("q=" + q)
        println("name=" + name)
        c.Expr[FieldContext[T, U]](
          q"""
             com.sksamuel.monkeytail.FieldContext(${name.decodedName.toString}, $extractor, ${c.prefix})
           """
        )
    }
  }
}