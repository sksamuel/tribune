
## Opaque Types, constructed by parsers

There should be no other way to construct a value of

1) `com.sksamuel.tribune.examples.valueclass.ParsedBook` (in the case of using `value class`)

2) `com.sksamuel.tribune.examples.dataclass.Book` (in the case of using `data class`)

_outside_ of the gradle-module `tribune-examples-model`.

Hence each of these example types has its constructor declared as `internal`
and offers the user parser functions, which are able to construct valid instances.
