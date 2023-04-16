package io.bluetape4k.ranges.impl

import io.bluetape4k.ranges.OpenClosedRange

data class DefaultOpenClosedRange<T: Comparable<T>>(
    override val startExclusive: T,
    override val endInclusive: T,
): OpenClosedRange<T> {

    override fun toString(): String = "($startExclusive..$endInclusive]"
}
