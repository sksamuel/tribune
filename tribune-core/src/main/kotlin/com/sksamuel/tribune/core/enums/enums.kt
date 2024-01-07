package com.sksamuel.tribune.core.enums

import arrow.core.Either
import arrow.core.leftNel
import arrow.core.right
import com.sksamuel.tribune.core.Parser
import com.sksamuel.tribune.core.transformEither
import kotlin.reflect.KClass

/**
 * Wraps a [Parser] that produces Strings to one that produces a value of a given enum.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 */
inline fun <I, reified ENUM : Enum<ENUM>, E> Parser<I, String, E>.enum(noinline ifError: (String) -> E): Parser<I, ENUM, E> {
   val kclass: KClass<ENUM> = ENUM::class
   return enum(kclass, ifError)
}

/**
 * Wraps a [Parser] that produces Strings to one that produces a value of a given enum.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 *
 * @param enumClass the KClass corresponding to the enum type.
 */
fun <I, T : Enum<T>, E> Parser<I, String, E>.enum(
    enumClass: KClass<T>,
    ifError: (String) -> E,
): Parser<I, T, E> {
    return transformEither { symbol ->
        runCatching { enumClass.java.enumConstants.first { it.name == symbol } }
            .fold({ it.right() }, { ifError(symbol).leftNel() })
    }
}

/**
 * Wraps a [Parser] that produces nullable Strings to one that produces nullable enums.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 * If the String is null, then null is produced.
 */
@JvmName("enumOrNull")
inline fun <I, reified ENUM : Enum<ENUM>, E> Parser<I, String?, E>.enum(noinline ifError: (String) -> E): Parser<I, ENUM?, E> {
   val kclass: KClass<ENUM> = ENUM::class
   return enum(kclass, ifError)
}

/**
 * Wraps a [Parser] that produces nullable Strings to one that produces nullable enums.
 * If the String is not a valid enum value, then an error is produced using [ifError].
 * If the String is null, then null is produced.
 */
@JvmName("enumOrNull")
fun <I, T : Enum<T>, E> Parser<I, String?, E>.enum(
    enumClass: KClass<T>,
    ifError: (String) -> E
): Parser<I, T?, E> {
    return transformEither { symbol ->
        if (symbol == null) Either.Right(null)
        else runCatching { enumClass.java.enumConstants.first { it.name == symbol } }
            .fold({ it.right() }, { ifError(symbol).leftNel() })
    }
}
