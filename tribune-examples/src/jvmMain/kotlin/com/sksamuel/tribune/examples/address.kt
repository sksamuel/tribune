package com.sksamuel.tribune.examples

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.compose
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.strings.length
import com.sksamuel.tribune.core.strings.nonBlankString

data class Address(
   val city: City,
   val zip: Zipcode,
   val country: CountryCode,
)

data class City(val value: String)
data class Zipcode(val value: String)
data class CountryCode(val value: String)

val cityParser = Parser
   .nonBlankString { "City must be provided" }
   .map { City(it) }

val zipcodeParser = Parser
   .nonBlankString { "Zipcode must be provided" }
   .length(5) { "Zipcode should be 5 digits" }
   .map { Zipcode(it) }

val countryCodeParser = Parser
   .nonBlankString { "CountryCode must be provided" }
   .length(2) { "CountryCode should be 2 digits" }
   .map { CountryCode(it) }

val addressParser = Parser.compose(
   cityParser,
   zipcodeParser,
   countryCodeParser,
   ::Address
)
