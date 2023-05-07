package io.bluetape4k.utils.math.integration

import org.apache.commons.math3.analysis.integration.UnivariateIntegrator

/**
 * [Romberg Algorithm](https://mathworld.wolfram.com/RombergIntegration.html) 을 이용하여 적분을 수행합니다.
 */
class RombergIntegrator: AbstractIntegrator() {

    override val apacheIntegrator: UnivariateIntegrator =
        org.apache.commons.math3.analysis.integration.RombergIntegrator()

}
