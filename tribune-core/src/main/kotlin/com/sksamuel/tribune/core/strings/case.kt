package com.sksamuel.tribune.core.strings

import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.map
import com.sksamuel.tribune.core.mapIfNotNull

/**
 * Modifies an I -> String [Parser] by uppercasing the output string.
 *
 * @return the output of the underlying parser with the output uppercased.
 */
fun <I, E> Parser<I, String, E>.toUppercase(): Parser<I, String, E> = map { it.uppercase() }

/**
 * Modifies an I -> String? [Parser] by uppercasing the output string.
 *
 * @return the output of the underlying parser with the output uppercased.
 */
@JvmName("toUppercaseOrNull")
fun <I, E> Parser<I, String?, E>.toUppercase(): Parser<I, String?, E> = mapIfNotNull { it.uppercase() }

/**
 * Modifies an I -> String [Parser] by lowercasing the output string.
 *
 * @return the output of the underlying parser with the output lowercased.
 */
fun <I, E> Parser<I, String, E>.toLowercase(): Parser<I, String, E> = map { it.lowercase() }

/**
 * Modifies an I -> String? [Parser] by lowercasing the output string.
 *
 * @return the output of the underlying parser with the output lowercased.
 */
@JvmName("toLowercaseOrNull")
fun <I, E> Parser<I, String?, E>.toLowercase(): Parser<I, String?, E> = mapIfNotNull { it.lowercase() }
