package com.sksamuel.optio.examples

import com.sksamuel.optio.core.parsers.Parser
import com.sksamuel.optio.core.parsers.allowNulls
import com.sksamuel.optio.core.parsers.filter
import com.sksamuel.optio.core.parsers.list
import com.sksamuel.optio.core.parsers.map
import com.sksamuel.optio.core.parsers.notNullOrBlank

data class HashTagsInput(
   val tags: List<String?>?,
)

data class HashTag(val value: String)
data class ParsedHashTags(val tags: HashTag)

val hashtagParser: Parser<String?, HashTag, String> =
   Parser.from<String?>()
      .notNullOrBlank { "Hashtag must be provided" }
      .filter({ it.startsWith("#") }) { "Author must be at least two names" }
      .map { HashTag(it) }

val hashtagsParser: Parser<List<String?>?, List<HashTag>?, String> =
   Parser.list(hashtagParser)
      .allowNulls()
