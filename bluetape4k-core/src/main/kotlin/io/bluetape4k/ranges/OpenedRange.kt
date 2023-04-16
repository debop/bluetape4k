package io.bluetape4k.ranges

/**
 * 값의 범위를 표현합니다. `[start, end)` 범위로 `end`는 포함되지 않습니다.
 *
 * @see ClosedRange
 */
interface OpenedRange<T: Comparable<T>> {
    /**
     * Minimum value in the range.
     */
    val start: T

    /**
     * Maximum value inf the range (exclusive)
     */
    val end: T

    /**
     * Checks whether the specified [value] belongs to the range
     */
    operator fun contains(value: T): Boolean = value >= start && value < end

    /**
     * Check whether the range is empty.
     */
    fun isEmpty(): Boolean = start >= end
}
