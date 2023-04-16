package io.bluetape4k.ranges

interface ClosedOpenRange<T: Comparable<T>>: Range<T> {

    val startInclusive: T
    val endExclusive: T

    override val first: T get() = startInclusive
    override val last: T get() = endExclusive

    override fun contains(value: T): Boolean =
        value >= startInclusive && value < endExclusive

    override fun isEmpty(): Boolean = startInclusive >= endExclusive
}
