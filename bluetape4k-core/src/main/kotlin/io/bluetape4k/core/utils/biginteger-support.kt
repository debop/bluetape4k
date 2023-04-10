package io.bluetape4k.core.utils

import java.math.BigInteger

/**
 * [Number]를 [BigInteger]로 변환합니다.
 */
fun Number.toBigInt(): BigInteger = when (this) {
    is BigInteger -> this
    is Int -> BigInteger.valueOf(this.toLong())
    is Long -> BigInteger.valueOf(this)
    else -> BigInteger(this.toString())
}

operator fun BigInteger.plus(other: Number): BigInteger = this.add(other.toBigInt())
operator fun BigInteger.minus(other: Number): BigInteger = this.subtract(other.toBigInt())
operator fun BigInteger.times(other: Number): BigInteger = this.multiply(other.toBigInt())
operator fun BigInteger.div(other: Number): BigInteger = this.divide(other.toBigInt())

operator fun BigInteger.compareTo(other: Number): Int = this.compareTo(other.toBigInt())

operator fun <T : Number> T.times(other: BigInteger): BigInteger = other.times(this)

/**
 * [BigInteger] 컬렉션의 모든 요소의 합을 구합니다.
 */
@JvmName("sumOfBigInteger")
fun Iterable<BigInteger>.sum(): BigInteger {
    var sum = BigInteger.ZERO
    for (element in this) {
        sum += element
    }
    return sum
}

/**
 * [BigInteger] 컬렉션의 평균값을 구합니다.
 */
@JvmName("averageOfBigInteger")
fun Iterable<BigInteger>.average(): Double {
    var sum = BigInteger.ZERO
    var count = 0
    for (element in this) {
        sum += element
        count++
    }
    return sum.toDouble() / count
}
