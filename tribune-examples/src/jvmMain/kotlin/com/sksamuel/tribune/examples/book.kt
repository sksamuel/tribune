package com.sksamuel.tribune.examples

import com.sksamuel.tribune.examples.opaque.input.BookInputData

data class BookInput(
   override val title: String?,
   override val author: String?,
   override val isbn: String?,
): BookInputData
