package io.bluetape4k.ranges.impl

import io.bluetape4k.ranges.ClosedOpenRange

data class ClosedOpenDoubleRange(
    override val startInclusive: Double,
    override val endExclusive: Double,
): ClosedOpenRange<Double>


infix fun Double.closedOpenRange(endExclusive: Double): ClosedOpenDoubleRange =
    ClosedOpenDoubleRange(this, endExclusive)
