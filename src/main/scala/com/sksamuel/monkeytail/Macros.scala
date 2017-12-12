package com.sksamuel.monkeytail

import cats.data.NonEmptyList

import scala.language.experimental.macros
import scala.language.implicitConversions

trait Path {
  def value: String
  // returns just the final part of the path
  def field: String
}

object NoPath extends Path {
  override def value = ""
  override def field = ""
}

case class FieldPath(components: NonEmptyList[String]) extends Path {
  override def value: String = components.toList.mkString(".")
  override def field: String = components.last
}

object FieldPath {
  def apply(components: String*): FieldPath = FieldPath(NonEmptyList.fromListUnsafe(components.toList))
}

object Macros {

  def test[T](c: scala.reflect.macros.whitebox.Context)
             (test: c.Expr[T => Boolean])(implicit builder: ViolationBuilder[T] = BasicViolationBuilder): c.Expr[RuleValidator[T]] = {
    import c.universe._

    c.Expr[RuleValidator[T]](
      q"""
             ${c.prefix}
           """
    )
  }

  def fieldContext[T, U](c: scala.reflect.macros.whitebox.Context)
                        (extractor: c.Expr[T => U]): c.Expr[FieldContext[T, U]] = {
    import c.universe._

    def recursePath(tree: Tree): Seq[String] = tree match {
      case Select(qualifier, name) => recursePath(qualifier) :+ name.decodedName.toString
      case _ => Nil
    }

    println(c.internal)
    println(c.prefix)
    extractor.tree match {
      case Function(_, selector) =>
        val path = recursePath(selector)
        c.Expr[FieldContext[T, U]](
          q"""
             com.sksamuel.monkeytail.FieldContext($extractor, ${c.prefix}, com.sksamuel.monkeytail.FieldPath(..$path))
           """
        )
    }
  }
}