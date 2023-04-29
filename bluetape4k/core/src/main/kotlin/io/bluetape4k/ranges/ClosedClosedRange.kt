package io.bluetape4k.ranges

interface ClosedClosedRange<T: Comparable<T>>: Range<T>, ClosedRange<T> {

    val startInclusive: T
    override val endInclusive: T

    override val first: T get() = startInclusive
    override val last: T get() = endInclusive

    override fun contains(value: T): Boolean =
        value >= startInclusive && value <= endInclusive

    override fun isEmpty(): Boolean =
        startInclusive > endInclusive
}
