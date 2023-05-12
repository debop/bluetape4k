package io.bluetape4k.utils.math.ml.clustering

import org.apache.commons.math3.ml.clustering.DoublePoint
import org.eclipse.collections.api.DoubleIterable


fun doublePointOf(x: Double, y: Double): DoublePoint =
    DoublePoint(doubleArrayOf(x, y))

fun doublePointOf(values: DoubleArray): DoublePoint = DoublePoint(values)

fun <N: Number> doublePointOf(vararg values: N): DoublePoint =
    DoublePoint(values.map { it.toDouble() }.toDoubleArray())

fun DoubleArray.toDoublePoint(): DoublePoint = DoublePoint(this)
fun DoubleIterable.toDoublePoint(): DoublePoint = DoublePoint(this.toArray())
