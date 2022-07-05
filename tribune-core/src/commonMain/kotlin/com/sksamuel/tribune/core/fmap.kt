package com.sksamuel.tribune.core

import arrow.core.ValidatedNel
import arrow.core.andThen

/**
 * Returns a [Parser] that maps the result of this parser by invoking the given function [f]
 * when the underlying parser returns a valid instance.
 *
 * This function performs no validation. It simply modifies the output from the underlying parser.
 *
 * @param f the function invoked to map the output of the underlying parser.
 *
 * @return a parser which returns the modified result of this parser.
 */
fun <I, A, B, E> Parser<I, A, E>.map(f: (A) -> B): Parser<I, B, E> =
   Parser { this@map.parse(it).map(f) }

/**
 * Maps a [Parser] that returns A? to a parser that returns B? by applying a function [f]
 * if the output is not null. If the output of the underlying parser is null, then null is
 * returned.
 *
 * @param f the function invoked to map the non-null output of the underlying parser.
 *
 * @return a parser which returns the modified result of this parser.
 */
fun <I, A, B, E> Parser<I, A?, E>.mapIfNotNull(f: (A) -> B): Parser<I, B?, E> =
   Parser { input -> this@mapIfNotNull.parse(input).map { if (it == null) null else f(it) } }

/**
 * Returns a [Parser] that maps the result of this parser by invoking the given function [f]
 * and flattening the output of that function.
 *
 * @param f the function invoked to map the output of the underlying parser.
 *
 * @return a parser which returns the modified and flattened result of this parser.
 */
fun <I, A, B, E> Parser<I, A, E>.flatMap(f: (A) -> ValidatedNel<E, B>): Parser<I, B, E> =
   Parser { this@flatMap.parse(it).andThen(f) }
