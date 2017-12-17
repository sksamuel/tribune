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

  def notnull[T <: Product : c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context): c.Expr[Validator[T]] = {
    import c.universe._

    val tpe = weakTypeOf[T]
    val fields = tpe.typeSymbol.asClass.primaryConstructor.asMethod.paramLists.flatten.map(_.name.decodedName.toString).zipWithIndex
    c.Expr[Validator[T]](
      q"""
       import cats.data._
       new com.sksamuel.monkeytail.Validator[$tpe] {
         override def apply(t: $tpe): Validated[NonEmptyList[com.sksamuel.monkeytail.Violation], $tpe] = {
           val violations = List(..$fields).flatMap { case (field, k) =>
             if (t.productElement(k) == null) Some(DefaultViolation(field + " should not be null", Path(field))) else None
           }
           if (violations.isEmpty) Validated.Valid(t) else Validated.Invalid(NonEmptyList.fromListUnsafe(violations))
         }
       }
     """
    )
  }

  def seqContext[T, U](c: scala.reflect.macros.whitebox.Context)
                      (extractor: c.Expr[T => Seq[U]]): c.Expr[SeqContext[T, U]] = {
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
             com.sksamuel.monkeytail.SeqContext($extractor, ${c.prefix}.wrapped, com.sksamuel.monkeytail.Path(..$path))
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

  def sanitizeContext[T <: Product : c.WeakTypeTag, U: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)
                                                                     (extractor: c.Expr[T => U]): c.Expr[SanitizeContext[T, U]] = {
    import c.universe._
    val tpeT = weakTypeOf[T]
    val tpeU = weakTypeOf[U]

    extractor.tree match {
      case Function(_, Select(_, name)) =>
        val term = TermName(name.decodedName.toString)
        c.Expr[SanitizeContext[T, U]](
          q"""
             com.sksamuel.monkeytail.SanitizeContext($extractor, ${c.prefix}.wrapped, (t: $tpeT, u: $tpeU) => t.copy($term = u))
           """
        )
    }
  }
}