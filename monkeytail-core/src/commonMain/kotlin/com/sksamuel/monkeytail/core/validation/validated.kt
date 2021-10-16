package com.sksamuel.monkeytail.core.validation

sealed class Validated<out E, out A> {

   abstract val isValid: Boolean
   abstract val isInvalid: Boolean

   data class Invalid<E>(val errors: List<E>) : Validated<E, Nothing>() {
      override val isInvalid: Boolean = true
      override val isValid: Boolean = false
   }

   data class Valid<A>(val value: A) : Validated<Nothing, A>() {
      override val isInvalid: Boolean = false
      override val isValid: Boolean = true
   }

   companion object {
      operator fun <A> invoke(value: A) = Valid(value)
      fun <E> invalid(error: E) = Invalid(listOf(error))
   }

   inline fun <B> map(f: (A) -> B): Validated<E, B> = when (this) {
      is Valid -> f(this.value).valid()
      is Invalid -> this
   }

   inline fun <F> mapError(f: (List<E>) -> List<F>): Validated<F, A> = when (this) {
      is Valid -> this
      is Invalid -> f(this.errors).invalid()
   }

   /**
    * Returns the value of this Validated if it is an instance of Valid.
    * Otherwise, throws an [IllegalStateException].
    */
   fun getUnsafe(): A = when (this) {
      is Invalid -> throw IllegalStateException("Not a valid instance, was $this")
      is Valid -> this.value
   }

   /**
    * Returns the errors contained in this Validated if it is an instance of Invalid.
    * Otherwise, throws an [IllegalStateException].
    */
   fun getErrorsUnsafe(): List<E> = when (this) {
      is Invalid -> this.errors
      is Valid -> throw IllegalStateException("Not a invalid instance, was $this")
   }

   inline fun <B> fold(ifInvalid: (List<E>) -> B, ifValid: (A) -> B): B = when (this) {
      is Invalid -> ifInvalid(errors)
      is Valid -> ifValid(value)
   }

   inline fun onValid(f: (A) -> Unit): Validated<E, A> {
      fold({}, { f(it) })
      return this
   }

   inline fun onInvalid(f: (List<E>) -> Unit): Validated<E, A> {
      if (this is Invalid<E>) f(this.errors)
      return this
   }

   inline fun toResult(f: (List<E>) -> Throwable): Result<A> {
      return when (this) {
         is Invalid<E> -> Result.failure<Nothing>(f(this.errors))
         is Valid<A> -> Result.success(this.value)
      }
   }

   fun getOrNull(): A? = fold({ null }, { it })
}

fun <A> Result<A>.toValidated(): Validated<Throwable, A> = fold({ it.valid() }, { it.invalid() })

inline fun <A, B, E> Validated<E, A>.flatMap(f: (A) -> Validated<E, B>): Validated<E, B> = when (this) {
   is Validated.Valid -> f(this.value)
   is Validated.Invalid -> this
}

inline fun <A, E> Validated<E, A>.getOrElse(f: (List<E>) -> A): A = when (this) {
   is Validated.Invalid -> f(this.errors)
   is Validated.Valid -> this.value
}

fun <ERROR, A> List<Validated<ERROR, A>>.traverse(): Validated<ERROR, List<A>> {
   val errors = this.filterIsInstance<Validated.Invalid<ERROR>>().flatMap { it.errors }
   return if (errors.isNotEmpty()) errors.invalid() else this.map { it.getUnsafe() }.valid()
}

fun <A> A.valid(): Validated<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): Validated<E, Nothing> = Validated.Invalid(listOf(this))
fun <E> List<E>.invalid(): Validated<E, Nothing> = Validated.Invalid(this)
