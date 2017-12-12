Monkeytail
=========================

Validation utilities for cats.

[![Build Status](https://travis-ci.org/sksamuel/monkeytail.png?branch=master)](https://travis-ci.org/sksamuel/monkeytail)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.monkeytail/monkeytail_2.11.svg?label=latest%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22monkeytail_2.11%22)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.monkeytail/monkeytail_2.12.svg?label=latest%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22monkeytail_2.12%22)

#### Introduction

This project is similar to other excellent projects like []https://github.com/wix/accord] and []https://github.com/tobnee/DValidation] 
but differs in that it focuses entirely on supporting cats.

The main abstraction in monkeytail is the `Validator[T]` trait which validates instances of T using cats.Validation.

```scala
trait Validator[T] {
  def apply(t: T): Validated[NonEmptyList[Violation], T]
}
```

You will notice that the error type used in the Validation is the `Violation` trait. This simple trait provides a means
to build up domain specific strongly typed errors and is used throughout monkeytail.

```scala
trait Violation {
  def message: String
}
```

Monkeytail offers macro based builders to quickly build instances of Validator[T] or you can always roll your own. 
Validators can be combined using the provided instances of `Monoid[Validator[T]]`.

#### Getting Started

Let's declare a class we want to validate. Yes I'm a [Star Trek fan](http://memory-alpha.wikia.com/wiki/Star_Trek:_Discovery).

```scala
case class Starship(name: String, maxWarp: Double)
```

We can always implement an instance of `Validator[Starship]` ourselves, in the boilerplate manual way.

```scala
val validator = Validator.simple[Starship] { starship =>
  starship.maxWarp < 10 && starship.name != null
}
```

That's fine for simple cases, but really we want to remove as much boilerplate as possible. 
So let's use the validator builder which operates field by field and builds up a validator instance for us.

```scala
val validator = Validator[Starship]
  .field(_.name)(name => name != null && name.startsWith("NCC") || name.startsWith("NX"))
  .field(_.maxWarp)(_ < 10)
```

As you will already know, no ship can exceed warp 10, so we must validate that. And of course, a ship without the prefix NCC or NX is not a real starship.  

We can now invoke this validator, as follows:

```scala
val enterprise = Starship("Enterprise", 9.6) 
validator(enterprise) == Valid(enterprise)
```

And if we provide some erroneous input we'll get back the appropriate accumulated Invalid.

```scala
validator(Starship(null, 11)) == 
  Invalid(
    NonEmptyList.of(
      BasicViolation("name has invalid value: null"), 
      BasicViolation("maxWarp has invalid value: 11.0")
    ))
```

What's very nice here is that the error messages automatically include the correct field name for us. The macro is taking care of that behind the scenes.

It will work for nested paths as well, if you did something like `field(_.address.postcode)(_.length == 8)`, then the
error would include the path `address.postcode`

#### Custom Errors

As mentioned earlier, the type used for errors is `Violation` and we can provide our own instances of `Violation` when
errors occur. This allows us to introduce a richer type system for errors as well as customize the actual error message, 
rather than the default `"$field has invalid value: $value"`. 

Let's create a custom error for ships that dare to exceed warp 10.

```scala
object MaxWarpExceededViolation extends Violation {
  override def message = "Max Warp Exceeded"
}
```

Now we can include this when we add a validation rule and when we validate our instances, we'll get back the
proper type.

```scala
val validator = Validator[Starship]
  .field(_.name)(_ != null)
  .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
  
validator(Starship("Enterprise", 11)) shouldBe Invalid(NonEmptyList.of(MaxWarpExceededViolation))
```
