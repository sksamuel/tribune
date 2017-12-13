Monkeytail
=========================

Validation utilities for cats. Licensed Apache 2.

[![Build Status](https://travis-ci.org/sksamuel/monkeytail.png?branch=master)](https://travis-ci.org/sksamuel/monkeytail)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.monkeytail/monkeytail_2.11.svg?label=latest%20release%20for%202.11"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22monkeytail_2.11%22)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.monkeytail/monkeytail_2.12.svg?label=latest%20release%20for%202.12"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22monkeytail_2.12%22)

### Introduction

This project is similar to other excellent projects like [https://github.com/wix/accord](https://github.com/wix/accord) and [https://github.com/tobnee/DValidation](https://github.com/tobnee/DValidation) 
but differs in that it focuses entirely on supporting [cats](https://typelevel.org/cats).

The main abstraction in monkeytail is the `Validator[T]` trait which validates instances of `T` using `cats.Validation`.

```scala
trait Validator[T] {
  def apply(t: T): Validated[NonEmptyList[Violation], T]
}
```

You will notice that the error type used in the Validation is the `Violation` trait. This simple trait provides a means
to build up domain specific strongly typed errors and is used throughout monkeytail.

Monkeytail offers macro based builders to quickly build instances of `Validator[T]` or you can always roll your own. 
Validators can be combined using the provided instances of `Monoid[Validator[T]]`.

### Getting Started

Let's declare a class we want to validate. Yes I'm a [Star Trek fan](http://memory-alpha.wikia.com/wiki/Star_Trek:_Discovery).

```scala
case class Starship(name: String, maxWarp: Double)
```

We can always implement an instance of `Validator[Starship]` ourselves, in the boilerplatey manual way.

```scala
val validator = new Validator[Starship] { 
  override def apply(t: Starship): Validated[NonEmptyList[Violation], Starship] = {
    val v1 = if (t.name == null) Invalid(NonEmptyList(SimpleViolation("Name cannot be null"))) else Valid(t)
    val v2 = if (t.maxWarp < 10) Valid(t) else Invalid(NonEmptyList(SimpleViolation("Exceeded max warp"))) 
    // combine validators here ..
  }
}
```

That's fine for simple cases, but really we want to remove as much boilerplate as possible, otherwise what's
the point of this project? So let's use the provided validator utilties which allows us to build up validation field by field.

Creating an empty validator just involves invoking apply on the `Validator` object. Then, make sure you have imported `monkeytail.ValidatorSyntax._` to enable the magic functions on the validator instance.

The main utility is in the `field` method, which accepts an extractor function to extract
the value we want to test, and then a test function that validates it and returns a bool if it is valid. 
The test expression can be as simple or as complicated as you want.

```scala
val validator = Validator[Starship]
  .field(_.name)(_ != null)
  .field(_.maxWarp)(_ < 10)
```

As you will already know, no ship can exceed warp 10, so we must validate that. 
And of course, a ship without a name is invalid. We can now invoke this validator, like this:

```scala
val enterprise = Starship("Enterprise", 9.6) 
validator(enterprise) == Valid(enterprise)
```

And if we provide some erroneous input we'll get back the appropriate accumulated Invalid.

```scala
validator(Starship(null, 11)) == 
  Invalid(
    NonEmptyList.of(
      SimpleViolation("name has invalid value: null"), 
      SimpleViolation("maxWarp has invalid value: 11.0")
    ))
```

What's nice here is that the error messages are automatically generated and include the correct field name for us. 
The macro is taking care of that behind the scenes.

It will work for nested paths as well, if you did something like `field(_.address.postcode)(_.length == 8)`, then the
error would include the path `address.postcode`

### Custom Errors

As mentioned earlier, the type used for errors is `Violation` and we can provide our own instances of `Violation` when
errors occur. This allows us to introduce a richer type system for errors as well as provide customized error messages
instead of the default `"$field has invalid value: $value"`.

At a basic level, a custom `Violation` can include no context at all.
Let's create a custom error type for ships that dare to exceed warp 10.

```scala
object MaxWarpExceededViolation extends Violation
```

Now we can include this when we add a validation rule and when we validate our instances, we'll get back the
proper type.

```scala
val validator = Validator[Starship]
  .field(_.name)(_ != null)
  .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
  
validator(Starship("Enterprise", 11)) == Invalid(NonEmptyList.of(MaxWarpExceededViolation))
```

We can add extra information to the violations if we wish. 
For this we will need a ViolationBuilder which is just a function that accepts the `Path` to the value
that generated the error, and the value that was under test. What we include in the violation type is up to us.
 
In the following example we're including a custom message using the value that failed. 

```scala

case class MaxWarpExceededViolation(message: String) extends Violation

object MaxWarpExceededViolationBuilder extends ViolationBuilder[Double] {
  override def apply(path: Path, value: Double) = MaxWarpExceededViolation(s"Max warp exceeded, was $value")
}

val validator = Validator[Starship]
  .field(_.name)(_ != null)
  .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolationBuilder)
  
validator(Starship("Enterprise", 11)) == Invalid(NonEmptyList.of(MaxWarpExceededViolation("Max warp exceeded, was 11.0")))
```

### Nested Validators

When we have a type that contains another type, and we already have a validator for the nested type,
we can tell monkeytail to just use the existing validator and delegate to that.

For example, if our starship model was like this:

```scala
case class Designation(prefix: String, code: String)
case class Starship(name: String, maxWarp: Double, designation: Designation)
```

We could define a validator for the `Designation` class separately (perhaps we use it in multiple places).

```scala
implicit val designationValidator = Validator[Designation]
  .field(_.prefix)(prefix => prefix != null && (prefix.startsWith("NCC") || prefix.startsWith("NX"))
  .field(_.code)(_ != null)
```

Then we we can define a validator for the `Starship` class and use the previous validator automatically
for the `designation` field, assuming it is available as an implicit in the current scope. Note, we use
the `valid` method rather than the `field` method we've been using so far.

```scala
val designationValidator = Validator[Starship]
  .field(_.name)(_ != null)
  .field(_.maxWarp)(_ < 10)(MaxWarpExceededViolation)
  .valid(_.designation) // this will require the previous implicit.
```

### Validating sequences

At the moment support for sequences is very basic and will be improved for 1.0 final.
You can use an existing validator and pass that into the `forall` method to validate each
element of the sequence individually, accumulating errors as elements fail.

```scala
case class Friend(name: String)
case class User(username: String, friends: Seq[Friend])

val friendValidator = Validator[Friend].field(...) // etc

// reuse the existing validator
val userValidator = Validator[User]
    .forall(_.friends, friendValidator) 
```
Or you can validate them manually using a regular test expression, like this:

```scala
Validator[User]
    .forall(_.friends)(_.name != null)
```

By using the `forall` method on the validator builder, you can pass in a test expression for a single element, 
and then this expression will be evaluated in turn for every element of the sequence. Every failing element
will be included in the resulting `cats.Validated` instance.

### Using Monkeytail in your project

For gradle users, add (replace 2.12 with 2.11 for Scala 2.11):

```groovy
compile 'com.sksamuel.elastic4s:elastic4s-core_2.12:x.x.x'
```

For SBT users add:

```scala
libraryDependencies += "com.sksamuel.monkeytail" %% "monkeytail" % "x.x.x"
```

For Maven users add (replace 2.12 with 2.11 for Scala 2.11):

```xml
<dependency>
    <groupId>com.sksamuel.monkeytail</groupId>
    <artifactId>monkeytail_2.12</artifactId>
    <version>x.x.x</version>
</dependency>
```

You will need to set the version by looking for the latest in maven central. You can click the links at the of the page.

### Building and Testing

This project is built with SBT. So to build
```
sbt compile
```

And to test
```
sbt test
```

### License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013-2016 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
