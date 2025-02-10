package com.sksamuel.tribune.core

fun <INPUT, OUTPUT, A, B, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   f: (A, B) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> = zip(p1, p2).map { (a, b) -> f(a, b) }

fun <INPUT, OUTPUT, A, B, C, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   f: (A, B, C) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> = zip(p1, p2, p3).map { (a, b, c) -> f(a, b, c) }

fun <INPUT, OUTPUT, A, B, C, D, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   f: (A, B, C, D) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> = zip(p1, p2, p3, p4).map { (a, b, c, d) -> f(a, b, c, d) }

fun <INPUT, OUTPUT, A, B, C, D, E, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   f: (A, B, C, D, E) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> = zip(p1, p2, p3, p4, p5).map { (a, b, c, d, e) -> f(a, b, c, d, e) }

fun <INPUT, OUTPUT, A, B, C, D, E, F, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   f: (A, B, C, D, E, F) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> = zip(p1, p2, p3, p4, p5, p6).map { (a, b, c, d, e, f) -> f(a, b, c, d, e, f) }

fun <INPUT, OUTPUT, A, B, C, D, E, F, G, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
   f: (A, B, C, D, E, F, G) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> =
   zip(p1, p2, p3, p4, p5, p6, p7).map { (a, b, c, d, e, f, g) -> f(a, b, c, d, e, f, g) }

fun <INPUT, OUTPUT, A, B, C, D, E, F, G, H, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
   p8: Parser<INPUT, H, ERROR>,
   f: (A, B, C, D, E, F, G, H) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> =
   zip(p1, p2, p3, p4, p5, p6, p7, p8).map { (a, b, c, d, e, f, g, h) -> f(a, b, c, d, e, f, g, h) }

fun <INPUT, OUTPUT, A, B, C, D, E, F, G, H, I, ERROR> Parser.Companion.compose(
   p1: Parser<INPUT, A, ERROR>,
   p2: Parser<INPUT, B, ERROR>,
   p3: Parser<INPUT, C, ERROR>,
   p4: Parser<INPUT, D, ERROR>,
   p5: Parser<INPUT, E, ERROR>,
   p6: Parser<INPUT, F, ERROR>,
   p7: Parser<INPUT, G, ERROR>,
   p8: Parser<INPUT, H, ERROR>,
   p9: Parser<INPUT, I, ERROR>,
   f: (A, B, C, D, E, F, G, H, I) -> OUTPUT,
): Parser<INPUT, OUTPUT, ERROR> =
   zip(p1, p2, p3, p4, p5, p6, p7, p8, p9).map { (a, b, c, d, e, f, g, h, i) ->
      f(a, b, c, d, e, f, g, h, i)
   }
