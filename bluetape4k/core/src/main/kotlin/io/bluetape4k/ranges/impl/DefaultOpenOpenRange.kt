package io.bluetape4k.ranges.impl

import io.bluetape4k.ranges.OpenOpenRange

data class DefaultOpenOpenRange<T: Comparable<T>>(
    override val startExclusive: T,
    override val endExclusive: T,
): OpenOpenRange<T> {

    override fun toString(): String = "($startExclusive..$endExclusive)"
}
