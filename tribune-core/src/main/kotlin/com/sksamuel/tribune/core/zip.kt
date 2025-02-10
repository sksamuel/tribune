package com.sksamuel.tribune.core

import arrow.core.Either
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9

fun <INPUT, A, B, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
): Parser<INPUT, Pair<A, B>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(p1.parse(input), p2.parse(input)) { a, b -> Pair(a, b) }
}

fun <INPUT, A, B, C, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
): Parser<INPUT, Triple<A, B, C>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(p1.parse(input), p2.parse(input), p3.parse(input)) { a, b, c -> Triple(a, b, c) }
}

fun <INPUT, A, B, C, D, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
): Parser<INPUT, Tuple4<A, B, C, D>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
      p2.parse(input),
      p3.parse(input),
      p4.parse(input),
   ) { a, b, c, d ->
      Tuple4(a, b, c, d)
   }
}

fun <INPUT, A, B, C, D, E, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
): Parser<INPUT, Tuple5<A, B, C, D, E>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
      p2.parse(input),
      p3.parse(input),
      p4.parse(input),
      p5.parse(input),
   ) { a, b, c, d, e ->
      Tuple5(a, b, c, d, e)
   }
}

fun <INPUT, A, B, C, D, E, F, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
): Parser<INPUT, Tuple6<A, B, C, D, E, F>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
      p2.parse(input),
      p3.parse(input),
      p4.parse(input),
      p5.parse(input),
      p6.parse(input),
   ) { a, b, c, d, e, f ->
      Tuple6(a, b, c, d, e, f)
   }
}

fun <INPUT, A, B, C, D, E, F, G, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
): Parser<INPUT, Tuple7<A, B, C, D, E, F, G>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
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

fun <INPUT, A, B, C, D, E, F, G, H, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
   p8: Parser<INPUT, H, ERROR>,
): Parser<INPUT, Tuple8<A, B, C, D, E, F, G, H>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
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

fun <INPUT, A, B, C, D, E, F, G, H, I, ERROR> Parser.Companion.zip(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
   p8: Parser<INPUT, H, ERROR>,
   p9: Parser<INPUT, I, ERROR>,
): Parser<INPUT, Tuple9<A, B, C, D, E, F, G, H, I>, ERROR> = Parser { input ->
   Either.zipOrAccumulate(
      p1.parse(input),
      p2.parse(input),
      p3.parse(input),
      p4.parse(input),
      p5.parse(input),
      p6.parse(input),
      p7.parse(input),
      p8.parse(input),
      p9.parse(input),
   ) { a, b, c, d, e, f, g, h, i ->
      Tuple9(a, b, c, d, e, f, g, h, i)
   }
}
