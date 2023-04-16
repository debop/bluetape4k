package io.bluetape4k.ranges

interface ClosedBigNumberRange<T: Comparable<T>>: ClosedRange<T> {

    override fun contains(value: T): Boolean = lessThanOrEquals(start, value) && lessThanOrEquals(value, endInclusive)
    override fun isEmpty(): Boolean = !lessThanOrEquals(start, endInclusive)

    /**
     * Compares two values of range domain type and returns true if first is less than or equal to second.
     */
    fun lessThanOrEquals(a: T, b: T): Boolean
}
