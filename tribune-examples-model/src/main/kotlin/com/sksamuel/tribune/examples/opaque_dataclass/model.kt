package com.sksamuel.tribune.examples.opaque_dataclass

data class Book internal constructor(
   val title: String,
   val author: String,
   val isbn: String,
)
