package com.sksamuel.monkeytail

import scala.language.experimental.macros
import scala.language.implicitConversions

// the components can be empty if the path is not specified
case class Path(components: List[String]) {
  def append(component: String) = Path(components :+ component)
  def withIndex(index: Int): Path = append(s"[$index]")
}

object Path {
  def apply(components: String*): Path = Path(components.toList)
  def empty = Path(Nil)
}

object Macros {

  def seqContext[T, U](c: scala.reflect.macros.whitebox.Context)
                      (extractor: c.Expr[T => U]): c.Expr[SeqContext[T, U]] = {
    import c.universe._

    def recursePath(tree: Tree): Seq[String] = tree match {
      case Select(qualifier, name) => recursePath(qualifier) :+ name.decodedName.toString
      case _ => Nil
    }

    extractor.tree match {
      case Function(_, selector) =>
        val path = recursePath(selector)
        c.Expr[SeqContext[T, U]](
          q"""
             com.sksamuel.monkeytail.FieldContext($extractor, ${c.prefix}.wrapped, com.sksamuel.monkeytail.Path(..$path))
           """
        )
    }
  }

  def fieldContext[T, U](c: scala.reflect.macros.whitebox.Context)
                        (extractor: c.Expr[T => U]): c.Expr[FieldContext[T, U]] = {
    import c.universe._

    def recursePath(tree: Tree): Seq[String] = tree match {
      case Select(qualifier, name) => recursePath(qualifier) :+ name.decodedName.toString
      case _ => Nil
    }

    extractor.tree match {
      case Function(_, selector) =>
        val path = recursePath(selector)
        c.Expr[FieldContext[T, U]](
          q"""
             com.sksamuel.monkeytail.FieldContext($extractor, ${c.prefix}.wrapped, com.sksamuel.monkeytail.Path(..$path))
           """
        )
    }
  }
}