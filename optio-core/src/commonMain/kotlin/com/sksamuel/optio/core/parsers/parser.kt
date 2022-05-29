package com.sksamuel.optio.core.parsers

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.zip

/**
 * A [Parser] is a function I => [ValidatedNel] that parses the input I.
 *
 * It is implemented as an interface to allow for variance on the type parameters.
 */
fun interface Parser<in I, out A, out E> {

   companion object {

      /**
       * Returns an identity [Parser] for a type I.
       *
       * This can be used as the entry point to building a parser. Eg,
       *
       * Parser<String>()...parse("mystring")
       */
      operator fun <I> invoke(): Parser<I, I, Nothing> = Parser { it.valid() }

      fun <INPUT, A, B, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
      ): Parser<INPUT, Pair<A, B>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input)) { a, b -> Pair(a, b) }
      }

      fun <INPUT, A, B, C, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
      ): Parser<INPUT, Triple<A, B, C>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input), p3.parse(input)) { a, b, c -> Triple(a, b, c) }
      }

      fun <INPUT, A, B, C, D, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
         p4: Parser<INPUT, D, ERROR>,
      ): Parser<INPUT, Tuple4<A, B, C, D>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input), p3.parse(input), p4.parse(input)) { a, b, c, d -> Tuple4(a, b, c, d) }
      }

      fun <INPUT, A, B, C, D, E, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
         p4: Parser<INPUT, D, ERROR>,
         p5: Parser<INPUT, E, ERROR>,
      ): Parser<INPUT, Tuple5<A, B, C, D, E>, ERROR> = Parser { input ->
         p1.parse(input).zip(p2.parse(input), p3.parse(input), p4.parse(input), p5.parse(input)) { a, b, c, d, e ->
            Tuple5(a, b, c, d, e)
         }
      }

      fun <INPUT, A, B, C, D, E, F, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
         p4: Parser<INPUT, D, ERROR>,
         p5: Parser<INPUT, E, ERROR>,
         p6: Parser<INPUT, F, ERROR>,
      ): Parser<INPUT, Tuple6<A, B, C, D, E, F>, ERROR> = Parser { input ->
         p1.parse(input).zip(
            p2.parse(input),
            p3.parse(input),
            p4.parse(input),
            p5.parse(input),
            p6.parse(input)
         ) { a, b, c, d, e, f ->
            Tuple6(a, b, c, d, e, f)
         }
      }

      fun <INPUT, A, B, C, D, E, F, G, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
         p4: Parser<INPUT, D, ERROR>,
         p5: Parser<INPUT, E, ERROR>,
         p6: Parser<INPUT, F, ERROR>,
         p7: Parser<INPUT, G, ERROR>,
      ): Parser<INPUT, Tuple7<A, B, C, D, E, F, G>, ERROR> = Parser { input ->
         p1.parse(input).zip(
            p2.parse(input),
            p3.parse(input),
            p4.parse(input),
            p5.parse(input),
            p6.parse(input),
            p7.parse(input),
         ) { a, b, c, d, e, f, g ->
            Tuple7(a, b, c, d, e, f, g)
         }
      }

      fun <INPUT, A, B, C, D, E, F, G, H, ERROR> compose(
         p1: Parser<INPUT, A, ERROR>,
         p2: Parser<INPUT, B, ERROR>,
         p3: Parser<INPUT, C, ERROR>,
         p4: Parser<INPUT, D, ERROR>,
         p5: Parser<INPUT, E, ERROR>,
         p6: Parser<INPUT, F, ERROR>,
         p7: Parser<INPUT, G, ERROR>,
         p8: Parser<INPUT, H, ERROR>,
      ): Parser<INPUT, Tuple8<A, B, C, D, E, F, G, H>, ERROR> = Parser { input ->
         p1.parse(input).zip(
            p2.parse(input),
            p3.parse(input),
            p4.parse(input),
            p5.parse(input),
            p6.parse(input),
            p7.parse(input),
            p8.parse(input),
         ) { a, b, c, d, e, f, g, h ->
            Tuple8(a, b, c, d, e, f, g, h)
         }
      }
   }

   fun parse(input: I): Validated<NonEmptyList<E>, A>

   fun <J> contramap(f: (J) -> I): Parser<J, A, E> =
      Parser { this@Parser.parse(f(it)) }

}

// helper functions for validated
fun <E, A> ValidatedNel<E, A>.getOrThrow(): A = fold({ error(it) }, { it })
fun <E, A> ValidatedNel<E, A>.getErrorsOrThrow(): NonEmptyList<E> = fold({ it }, { error(it.toString()) })
fun <A> A.valid(): ValidatedNel<Nothing, A> = Validated.Valid(this)
fun <E> E.invalid(): ValidatedNel<E, Nothing> = this.invalidNel()
fun <E> List<E>.invalid(): ValidatedNel<E, Nothing> = Validated.Invalid(Nel.fromListUnsafe(this))
