package com.sksamuel.optio.examples

import com.sksamuel.optio.core.Parser
import com.sksamuel.optio.core.allowNulls
import com.sksamuel.optio.core.filter
import com.sksamuel.optio.core.collections.list
import com.sksamuel.optio.core.map
import com.sksamuel.optio.core.notNullOrBlank

data class HashTagsInput(
   val tags: List<String?>?,
)

data class HashTag(val value: String)
data class ParsedHashTags(val tags: Set<HashTag>)

val hashtagParser: Parser<String?, HashTag, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Hashtag must be provided" }
      .filter({ it.startsWith("#") }) { "Author must be at least two names" }
      .map { HashTag(it) }

val hashtagsParser: Parser<List<String?>?, ParsedHashTags, String> =
   Parser.list(hashtagParser)
      .allowNulls()
      .map { ParsedHashTags(it?.toSet() ?: emptySet()) }

fun main() {
   println(hashtagsParser.parse(listOf(null, null, null, "foo")))
   println(hashtagsParser.parse(listOf("foo", "bar")))
}
