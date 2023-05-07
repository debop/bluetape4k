package io.bluetape4k.utils.math.interpolation

import io.bluetape4k.logging.KLogging
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator

abstract class AbstractInterpolator: Interpolator {

    companion object: KLogging() {
        const val MINIMUM_SIZE = 5
    }

    protected abstract val apacheInterpolator: UnivariateInterpolator

    override fun interpolate(xs: DoubleArray, ys: DoubleArray): (Double) -> Double {
        val interpolationFunc = apacheInterpolator.interpolate(xs, ys)
        return { x: Double -> interpolationFunc.value(x) }
    }
}
