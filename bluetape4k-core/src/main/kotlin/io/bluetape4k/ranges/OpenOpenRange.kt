package io.bluetape4k.ranges

interface OpenOpenRange<T: Comparable<T>>: Range<T> {

    val startExclusive: T
    val endExclusive: T

    override val first: T get() = startExclusive

    override val last: T get() = endExclusive

    override fun contains(value: T): Boolean =
        value > startExclusive && value < endExclusive

    override fun isEmpty(): Boolean =
        startExclusive >= endExclusive
}
