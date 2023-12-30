Changelog
=========================


### 2.0.0

* Updated to Arrow 1.2.x - Note: Arrow has deprecated `Validated`. In this release, `Parser` has been updated to use `EitherNel`.
* Moved some parsers to new packages.
* Removed all previously deprecated functions.
* Added variants of string parsers to work on nullable strings.

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
