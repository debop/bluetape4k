package io.bluetape4k.utils.math.interpolation

import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator

class SplineInterpolator: AbstractInterpolator() {

    override val apacheInterpolator: UnivariateInterpolator =
        org.apache.commons.math3.analysis.interpolation.SplineInterpolator()

}
