package io.bluetape4k.ranges

/**
 * 값의 범위를 나타내는 인터페이스입니다.
 */
interface Range<T: Comparable<T>> {

    /**
     * 범위의 하한 값
     */
    val first: T

    /**
     * 범위의 상한 값
     */
    val last: T

    operator fun contains(value: T): Boolean

    fun isEmpty(): Boolean
}
