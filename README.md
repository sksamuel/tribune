Tribune
=========================

_parse don't validate_ for Kotlin.

Inspired by [this blog post by Alexis King](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/),
Tribune is a Kotlin library for the JVM that builds on [Arrow](https://arrow-kt.io/) to provide a toolset for creating simple parsers from raw _input_ types, to properly
validated _parsed_ types.

[![master](https://github.com/sksamuel/tribune/actions/workflows/master.yml/badge.svg)](https://github.com/sksamuel/tribune/actions/workflows/master.yml)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.tribune/tribune-core.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ctribune)
[<img src="https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/com.sksamuel.tribune/tribune-core.svg?label=latest%20snapshot&style=plastic"/>](https://oss.sonatype.org/content/repositories/snapshots/com/sksamuel/tribune/tribune-core/)

See [Changelog](changelog.md)

### Rationale

Normally, when we have a system that accepts input, we validate or sanitize that input. That is, we run some checks on
the inputs,
return some kind of error if they don't meet our requirements. For example, we want emails to contain an '@' and a zip
code to be digits.
Then we continue with the request safe in the knowledge that we've done our due diligence.

Here is an extremely simplified example.

```kotlin
fun validate(email: String) = name.contains("@")

fun persist(email: String) {
   // write to db
}

fun handleRequest(email: String) {
   if (!validate(email)) error("Not a real email")
   persist(email)
}
```

But the ultimate actioner of the input (`persist` in the above example) has to take it on faith that the input was
validated, or it has to perform the validation again itself. Obviously in the 6 line example above, it is easy to see
that validation is taking place but as a code base grows in complexity, and the validation code drifts from the use
site,
it becomes less obvious what validation is taking place and where.

In my experience, as codebases grow, our 'service' methods performing the 'logic' end up being called from
more and more places. Perhaps new endpoints are added which ultimately call into the same service, or feature flags
are added which result in multiple paths sharing functions. As new developers onboard, they can distrust the existing
code or be unware of something added previously. How can we be sure that the validation is taking place at the right
places, for all the appropriate code paths?

Sometimes we do the validation again, "just to be sure". We don't trust that the callers of our code are giving us
properly validated types, so we check again, just in case. Never can be too safe amirite? In these situations developers
have moved the validation into the 'logic' method itself, now resulting in methods doing validation as well as
processing.

As input validation often results in errors being returned to the caller, the deeper in the stack we perform these
operations, the more boilerplate we need to bubble them back out. We can throw an exception and let it propagate out,
or thread a `Result` instance back to the caller, but in both cases our entry point has to disambiguate parse errors
from other errors, muddying the error handling.

We can rely on tests. That's how it's done in dynamic languages where you don't know the data type you're getting, so
you have to use faith and a solid test suite. But we're using a compiled language, and isn't a compiled language
supposed to leverage the compiler to create more robust code?

When we validate something, we are adding information. If we validate that a string is a valid email, we have added
the "is an email" assertion to the original string. When we validate and then continue with the original types,
we are not passing that extra information to the caller. Why aren't we using the rich type system of a compiled language
to help us catch validation errors?

If we indicate through types that our input had already been validated, then we could trust that
input. One way to do this is to have a type that represents the "checked and validated" result of the original input.

This is what we mean when we say _parsing not validating_.

### Parsers

In Tribune, a parser is a function
from an input type to a valid or invalid result. A valid result contains the _parsed_ type and should be a wrapper type
that indicates the extra validation that has been performed.
An invalid result
contains one or more errors in the form of a `NonEmptyList`. The actual type returned is an [Arrow](https://arrow-kt.io/) `EitherNel<E, O>.

A parser has three type parameters, the first being the input type, the second being the parsed type (or output type), and the third
being the error type. The error type can be your own ADT or just plain strings. In this example we will use strings.
The ADT approach is powerful when you want fine control over error handling, but if we are more interested in the
robustness factor than how errors are reported, strings will suffice.

We create a `Parser` from our input type - in this case a nullable `String`. Our initial parser is always
a pass-through parser that just returns the input as-is and which allows us to add further constraints.
Note that the error type is `Nothing` because on the pass through parser, there are no errors to report.

```kotlin
val parser: Parser<String?, String?, Nothing> = Parsers.nullableString
```

Next we can add more constraints with appropriate error messages. Each additional
constraint we add narrows the output type. For example, if we constrain a nullable string to disallow nulls, then the
parser's output type will narrow to be a non-nullable String. The input type does not change, because that is
representing our initial raw input. Note that the error type will change to the type of the error you provide, in
this case a string also.

```kotlin
val parser: Parser<String?, String, String> =
   Parsers.nullableString
      .notNullOrBlank { "Must be provided" }
```

We could further constrain this input to be an int:

```kotlin
val parser: Parser<String?, Int, String> =
   Parsers.nullableString
      .notNullOrBlank { "Must be provided" }
      .int { "must be int" }
```

There are many methods available on a parser, eg `filter`, `minlen`, `enum`, `contramap` and so on.
Explore in your IDE to see the full set.

#### Wrapping

Once we're finished with validation, we want to then wrap in a parsed type. We can do this with `map`:

```kotlin
val parser: Parser<String?, MyParsedType, String> =
   Parsers.nullableString
      .notNullOrBlank { "Must be provided" }
      .int { "must be int" }
      .map { MyParsedType(it) }
```

Parsers are invoked using the `parse` method. Eg:

```kotlin
parser.parse("abc") // must be int
parser.parser("123") // success!
```

Or if you want a null instead of errors, you can use `parseOrNull`. Eg:

```kotlin
parser.parseOrNull("abc") // must be an int, so null is returned
parser.parser("123") // success!
```

#### Full Example

We start by creating a type to represent a validated and sanitized value. Let's say we want to validate that input
strings are valid ISBN codes. They must be 10 or 13 digit codes, and 13 digit codes must start with a 9.
The parsed type will be called `Isbn`.

```kotlin
data class Isbn(val value: String) {
   init {
      require(value.length == 10 || value.length == 13)
      require(value.length == 10 || value.startsWith("9"))
   }
}
```

Next our parser will include the validation logic, ultimately wrapping in the `Isbn` type:

```kotlin
val isbnParser =
   Parsers.nullableString
      .notNullOrBlank { "ISBN must be provided" }
      .map { it.replace("-", "") } // remove dashes
      .length({ it == 10 || it == 13 }) { "Valid ISBNs have length 10 or 13" }
      .filter({ it.length == 10 || it.startsWith("9") }, { "13 Digit ISBNs must start with 9" })
      .map { Isbn(it) }
```

Then we can parse ISBN codes:

```kotlin
isbnParser.parse("9783161484100") // good!
isbnParser.parse("978-3-16-148410-0") // good!
isbnParser.parse("ABC-3-16-148410-0") // bad!
isbnParser.parse("978-3-16-148410") // bad!
```


#### Composing

We've seen how we can have a parser for a simple type, but most of the time we have complex types, and we want
to validate each field. We achieve this in Tribune using `Parser.compose`. This function accepts one or more parsers,
and then a _mapping_ function which combines all the valid results into a single type. If any component parser fails,
all
the errors will be combined and returned.

Note that this _mapping_ function can be the constructor of a data class for ease of use.

We must also provide an input type which has all the individual inputs wrapped together. In HTTP services, this
input type is often your deserialized type from the request.

For example, we will create parsers for cities, zips and countries and then combine them into a single address parser.

We start by creating a type to contain the non-validated inputs, and then the validated output type.

```kotlin
data class AddressInput(
   val city: String?,
   val zip: String?,
   val country: String?,
)

data class Address(
   val city: City,
   val zip: Zipcode,
   val country: CountryCode,
)

data class City(val value: String)
data class Zipcode(val value: String)
data class CountryCode(val value: String)
```

Next we create a parser for each field:

```kotlin
val cityParser = Parsers
   .nonBlankString { "City must be provided" }
   .map { City(it) }

val zipcodeParser = Parsers
   .nonBlankString { "Zipcode must be provided" }
   .length(5) { "Zipcode should be 5 digits" }
   .map { Zipcode(it) }

val countryCodeParser = Parsers
   .nonBlankString { "CountryCode must be provided" }
   .length(2) { "CountryCode should be 2 digits" }
   .map { CountryCode(it) }
```

Finally, we combine these together. Note the use of contramap here, this is how we _extract_ the appropriate field
from the input object to pass to each component parser.

```kotlin
val addressParser = Parser.compose(
   cityParser.contramap { it.city },
   zipcodeParser.contramap { it.zip },
   countryCodeParser.contramap { it.country },
   ::Address
)
```

Now we can use this parser like:

```kotlin
addressParser.parse(AddressInput("Chicago", "60011", "US")) // valid!
addressParser.parse(AddressInput("Chicago", "60ABC", "US")) // invalid!
addressParser.parse(AddressInput("Chicago", "60011", "Krypton")) // invalid!
addressParser.parse(AddressInput(null, "60011", "Krypton")) // invalid!
addressParser.parse(AddressInput(null, null, null)) // invalid!
```

### Ktor Integration

Tribune provides [Ktor](https://ktor.io) integration through the optional `tribune-ktor` module.

Once this is added to your build, you can use `withParsedBody` inside your Ktor routes. This function requires
a parser and an optional _error handler_. The request body is retrieved as an instance of the parser input type,
and then passed to the parser.

If the parser returns errors, the _error handler_ is invoked to return an error response to the caller. Tribune provides
several error handlers out of the box. A full list of provided error handlers is listed later in this document.

Here is an example of `withParsedBody`, reusing the earlier parser for ISBN book codes.

This parser is used inside a POST endpoint and if valid, we respond with a 201, otherwise the default
handler is used (returns 400 Bad Request with a JSON body of errors).

```kotlin
routing {
   post("/isbn") {
      withParsedBody(isbnParser) { isbn ->
         println("Parsed ISBN $isbn")
         call.respond(HttpStatusCode.Created)
      }
   }
}
```

This table lists the handlers provided out of the box:

| Handler             | Description                                                                                                                                                              |
|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `jsonHandler`       | Returns a 400 Bad Request with a JSON array, where each error is an element. Each error is converted to a String through .toString() before being included in the array. |
| `textPlainHandler`  | Returns a 400 Bad Request with a text/plain body, which is the list of errors concatented into a simple string                                                           |
| `loggingHandler`    | Writes the errors to info level logging, and does not return a response or body. This should be composed with another handler.                                           |
| `badRequestHandler` | Returns an error response as a 400 Bad Request without a body. This is suitable for when we don't want to return error details to the caller.                            |

Handlers can be composed together using the `compose` extension function on a handler.
Eg, to use the logging handler with the json handler, we can do:

```kotlin
withParsedBody(parser, loggingHandler.compose(jsonHandler)) { parsed ->
   println("Parsed input $parsed")
   call.respond(HttpStatusCode.OK)
}
```

### Using tribune in your project

```groovy
compile 'com.sksamuel.tribune:tribune-core:x.x.x'
```
