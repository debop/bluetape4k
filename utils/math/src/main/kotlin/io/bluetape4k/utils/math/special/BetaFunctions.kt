package io.bluetape4k.utils.math.special

import io.bluetape4k.core.assertPositiveNumber
import org.apache.commons.math3.special.Gamma.logGamma
import kotlin.math.exp

fun betaLn(x: Double, y: Double): Double {
    x.assertPositiveNumber("x")
    y.assertPositiveNumber("y")

    return logGamma(x) + logGamma(y) - logGamma(x + y)
}

fun betaLn(xs: Sequence<Double>, ys: Sequence<Double>): Sequence<Double> = sequence {
    val xe = xs.iterator()
    val ye = ys.iterator()
    while (xe.hasNext() && ye.hasNext()) {
        yield(betaLn(xe.next(), ye.next()))
    }
}

fun beta(x: Double, y: Double): Double = exp(betaLn(x, y))

fun beta(xs: Sequence<Double>, ys: Sequence<Double>): Sequence<Double> =
    betaLn(xs, ys).map { exp(it) }
