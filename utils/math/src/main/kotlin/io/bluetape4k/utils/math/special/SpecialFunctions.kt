package io.bluetape4k.utils.math.special

import org.apache.commons.math3.special.BesselJ
import org.apache.commons.math3.special.Beta
import org.apache.commons.math3.special.Erf
import org.apache.commons.math3.special.Gamma

private const val DEFAULT_EPSILON: Double = 1.0e-14

/**
 * Returns the first Bessel function, \(J_{order}(x)\).
 *
 * @param order Order of the Bessel function
 * @param x argument
 * @return Value of Bessel function of the first kind.
 */
fun besselj(order: Double, x: Double): Double = BesselJ.value(order, x)

fun logBeta(p: Double, q: Double): Double = Beta.logBeta(p, q)

fun regularizedBeta(
    x: Double,
    a: Double,
    b: Double,
    epsilon: Double = DEFAULT_EPSILON,
    maxIterations: Int = Int.MAX_VALUE,
): Double =
    Beta.regularizedBeta(x, a, b, epsilon, maxIterations)

fun erf(x: Double): Double = Erf.erf(x)

fun erf(x1: Double, x2: Double): Double = Erf.erf(x1, x2)

fun erfInv(x: Double): Double = Erf.erfInv(x)

fun erfc(x: Double): Double = Erf.erfc(x)

fun erfcInv(x: Double): Double = Erf.erfcInv(x)

fun gamma(x: Double): Double = Gamma.gamma(x)

fun digamma(x: Double): Double = Gamma.digamma(x)

fun trigamma(x: Double): Double = Gamma.trigamma(x)

fun logGamma(x: Double): Double = Gamma.logGamma(x)

fun logGamma1p(x: Double): Double = Gamma.logGamma1p(x)

fun regularizedGammaP(
    a: Double,
    x: Double,
    epsilon: Double = DEFAULT_EPSILON,
    maxIterations: Int = Int.MAX_VALUE,
): Double {
    return Gamma.regularizedGammaP(a, x, epsilon, maxIterations)
}

fun regularizedGammaQ(
    a: Double,
    x: Double,
    epsilon: Double = DEFAULT_EPSILON,
    maxIterations: Int = Int.MAX_VALUE,
): Double {
    return Gamma.regularizedGammaQ(a, x, epsilon, maxIterations)
}
