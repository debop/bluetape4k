package io.bluetape4k.ranges.impl

import io.bluetape4k.ranges.ClosedClosedRange

data class DefaultClosedClosedRange<T: Comparable<T>>(
    override val startInclusive: T,
    override val endInclusive: T,
): ClosedClosedRange<T>, ClosedRange<T> by startInclusive..endInclusive {

    override fun contains(value: T): Boolean =
        value >= startInclusive && value <= endInclusive

    override fun isEmpty(): Boolean = startInclusive >= endInclusive

    override fun toString(): String = "[$startInclusive..$endInclusive]"

}
