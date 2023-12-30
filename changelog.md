Changelog
=========================


### 2.0.0

* Updated to Arrow 1.2.x - Note: Arrow has deprecated `Validated`. In this release, `Parser` has been updated to use `EitherNel`.
* Updated to Kotlin 1.9.x
* Moved some parsers to new packages.
* Removed all previously deprecated functions.
* Added variants of string parsers to work on nullable strings.
* Added variants of int parsers to work on nullable ints.
* Added variants of long parsers to work on nullable longs.
* Added `nullIf` to convert a value to a null using a supplied predicate.
* Added `filterNulls()` on `Set` and `List` parsers to remove any elements that are null.
* Added improved enum syntax.
* Improved boolean parsers.
* Added `map` parsers for keys and values.
* Added `nullable()` to convert Parser of `I -> A` to `I? -> A?`
* Added more convenience builders to `Parsers`.
* Added `parseOrNull` as a convenience to return a success or null in the case of failure for any given input.

### 1.3.0

* Released support (and min version) for Kotlin 1.8.x
* Added arity-9 and 10 for compose.

### 1.2.4

* Added `fromNullableString` to Parser.
* Renamed `withParsedInput` to `withParsedBody`
* Added handler `compose`, `loggingHandler` and `badRequestHandler`.

#### 1.2.3

* Updated to Ktor2

#### 1.2.2

* Renamed to Tribune

#### 1.2.1

* Added set parser
* Renamed .repeated to .asList
* Removed internal custom valid/invalid extensions

#### 1.2.0

* Package renames

#### 1.1.1

* Added ktor output handler

#### 1.1.0

* Renamed project to Optio
* Added datetime module
* Added ktor module
* Added zip and compose
