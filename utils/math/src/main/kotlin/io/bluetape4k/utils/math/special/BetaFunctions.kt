package io.bluetape4k.utils.math.special

import io.bluetape4k.collections.eclipse.primitives.doubleArrayList
import io.bluetape4k.collections.eclipse.primitives.doubleArrayListOf
import io.bluetape4k.collections.eclipse.primitives.toDoubleArrayList
import io.bluetape4k.core.assertPositiveNumber
import org.apache.commons.math3.special.Gamma.logGamma
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList
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

fun betaLn(xs: Iterable<Double>, ys: Iterable<Double>): DoubleArrayList {
    val results = doubleArrayListOf()
    val xe = xs.iterator()
    val ye = ys.iterator()
    while (xe.hasNext() && ye.hasNext()) {
        results.add(betaLn(xe.next(), ye.next()))
    }
    return results
}


fun betaLn(xs: DoubleArray, ys: DoubleArray): DoubleArray {
    val minSize = minOf(xs.size, ys.size)
    return DoubleArray(minSize) {
        betaLn(xs[it], ys[it])
    }
}

fun betaLn(xs: DoubleArrayList, ys: DoubleArrayList): DoubleArrayList {
    val minSize = minOf(xs.size(), ys.size())
    return doubleArrayList(minSize) {
        betaLn(xs[it], ys[it])
    }
}

fun beta(x: Double, y: Double): Double = exp(betaLn(x, y))

fun beta(xs: Sequence<Double>, ys: Sequence<Double>): Sequence<Double> {
    return betaLn(xs, ys).map { exp(it) }
}

fun beta(xs: Iterable<Double>, ys: Iterable<Double>): DoubleArrayList {
    return betaLn(xs, ys).collect { exp(it) }.toDoubleArrayList()
}

fun beta(xs: DoubleArray, ys: DoubleArray): DoubleArray {
    val minSize = minOf(xs.size, ys.size)
    return DoubleArray(minSize) {
        beta(xs[it], ys[it])
    }
}

fun beta(xs: DoubleArrayList, ys: DoubleArrayList): DoubleArrayList {
    val minSize = minOf(xs.size(), ys.size())
    return doubleArrayList(minSize) {
        beta(xs[it], ys[it])
    }
}
