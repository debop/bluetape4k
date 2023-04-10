package io.bluetape4k.core.support

import java.math.BigDecimal


/**
 * [Number]를 [BigDecimal]로 변환합니다.
 */
fun Number.toBigDecimal(): BigDecimal = when (this) {
    is BigDecimal -> this
    is Long -> BigDecimal(this)
    else -> BigDecimal(this.asDouble().toString())
}

operator fun BigDecimal.plus(other: Number): BigDecimal = this.add(other.toBigDecimal())
operator fun BigDecimal.minus(other: Number): BigDecimal = this.subtract(other.toBigDecimal())
operator fun BigDecimal.times(other: Number): BigDecimal = this.multiply(other.toBigDecimal())
operator fun BigDecimal.div(other: Number): BigDecimal = this.divide(other.toBigDecimal())

operator fun BigDecimal.compareTo(other: Number): Int = this.compareTo(other.toBigDecimal())

operator fun <T : Number> T.times(other: BigDecimal): BigDecimal = other.times(this)

/**
 * BigDecimal의 모든 요소를 더한다
 */
@JvmName("sumOfBigDecimal")
fun Iterable<BigDecimal>.sum(): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

/**
 * BigDecimal 컬렉션의 평균을 구합니다.
 */
@JvmName("averageOfBigDecimal")
fun Iterable<BigDecimal>.average(): BigDecimal {
    var sum = BigDecimal.ZERO
    var count = 0
    for (element in this) {
        sum += element
        count++
    }
    return sum / count
}
