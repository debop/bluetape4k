package io.bluetape4k.ranges

/**
 * 하한은 포함 안되고, 상한은 포함되는 범위를 표현합니다. (`startExclusive`, `endInclusive]
 *
 * @property startExclusive 하한
 * @property endInclusive 상한
 */
interface OpenClosedRange<T: Comparable<T>>: Range<T> {

    val startExclusive: T
    val endInclusive: T

    override val first: T get() = startExclusive
    override val last: T get() = endInclusive

    override fun contains(value: T): Boolean =
        value > startExclusive && value <= endInclusive

    override fun isEmpty(): Boolean = endInclusive <= startExclusive
}
