package com.sksamuel.monkeytail

case class SeqContext[T, U](extractor: T => Seq[U],
                            parent: Validator[T], // the current validator we'll be combining with
                            path: Path) {

  def apply(test: U => Boolean)(implicit builder: ViolationBuilder[U] = DefaultViolationBuilder): Validator[T] = {
    val validator = Validator.seq[T, U](extractor, new DefaultValidator[U](test, builder))
    Validator.monoid[T].combine(parent, validator)
  }
}
