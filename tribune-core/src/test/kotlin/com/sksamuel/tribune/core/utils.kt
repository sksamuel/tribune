package com.sksamuel.tribune.core

import arrow.core.NonEmptyList
import arrow.core.ValidatedNel

// helper functions for validated
fun <E, A> ValidatedNel<E, A>.getOrThrow(): A = fold({ error(it) }, { it })
fun <E, A> ValidatedNel<E, A>.getErrorsOrThrow(): NonEmptyList<E> = fold({ it }, { error(it.toString()) })
