package com.sksamuel.monkeytail

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{FlatSpec, Matchers}

class ValidationTest extends FlatSpec with Matchers {

  "Validation Macros" should "build validator" in {

    implicit val parentValidator: Validator[Parent] = Validator[Parent]
      .field(_.id)(_ != null)

    implicit val commentValidator: Validator[Comment] = Validator[Comment]
      .field(_.text).validate(_.contains("ok"))(InvalidCommentText)

    val assetValidator = Validator[Asset]
      .field(_.name)(_ != null)(NameNullViolation)
      .field(_.name)(name => name != null && name.trim.length > 3)(InvalidNameViolation)
      .field(_.filetype)(name => name != null && name.trim.length > 3)
      .validate(_.comment)
      .validate(_.parents)

    assetValidator(Asset(null, "abcd", Comment("ok"))) shouldBe Invalid(NonEmptyList.of(NameNullViolation, DefaultViolation("Name must be longer than 3 characters but was null")))
    assetValidator(Asset("a", null, Comment("ok"))) shouldBe Invalid(NonEmptyList.of(DefaultViolation("Name must be longer than 3 characters but was a"), DefaultViolation("filetype has invalid value: null")))
    assetValidator(Asset("abcd", null, Comment("ok"))) shouldBe Invalid(NonEmptyList.of(DefaultViolation("filetype has invalid value: null")))
    assetValidator(Asset("abcd", "abcd", Comment("ok"))) shouldBe Valid(Asset("abcd", "abcd", Comment("ok")))
    assetValidator(Asset("abcd", "abcd", Comment("#"))) shouldBe Invalid(NonEmptyList.of(InvalidCommentText))
    assetValidator(Asset("abcd", "abcd", Comment("ok"), Seq(Parent(null), Parent(null)))) shouldBe
      Invalid(NonEmptyList.of(DefaultViolation("id has invalid value: null"), DefaultViolation("id has invalid value: null")))
  }
}

case object InvalidCommentText extends Violation {
  override def message = "Comment contains invalid text"
}

object InvalidNameViolation extends ViolationBuilder[String] {
  override def apply(name: String, value: String) = DefaultViolation(s"Name must be longer than 3 characters but was $value")
}

case object NameNullViolation extends Violation {
  override def message = "Name cannot be null"
}

case class InvalidStringViolation(message: String) extends Violation

case class Asset(name: String, filetype: String, comment: Comment, parents: Seq[Parent] = Nil)

case class Comment(text: String)

case class Parent(id: String)