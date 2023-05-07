package io.bluetape4k.utils.math.interpolation

fun interface Interpolator {

    fun interpolate(xs: DoubleArray, ys: DoubleArray): (Double) -> Double

}
