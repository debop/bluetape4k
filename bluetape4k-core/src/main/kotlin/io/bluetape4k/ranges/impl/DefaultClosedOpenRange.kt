package io.bluetape4k.ranges.impl

import io.bluetape4k.ranges.ClosedOpenRange

data class DefaultClosedOpenRange<T: Comparable<T>>(
    override val startInclusive: T,
    override val endExclusive: T,
): ClosedOpenRange<T> {

    override fun toString(): String = "[$startInclusive..$endExclusive)"
}
