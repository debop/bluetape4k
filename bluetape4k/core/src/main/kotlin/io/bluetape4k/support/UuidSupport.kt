package io.bluetape4k.support

import io.bluetape4k.utils.BigIntegerPair
import java.math.BigInteger
import java.util.*

fun BigInteger.toUuid(): UUID {
    val (most, least) = BigIntegerPair.unpair(this)
    return UUID(most.longValueExact(), least.longValueExact())
}

fun UUID.toBigInt(): BigInteger = BigIntegerPair.pair(
    this.mostSignificantBits.toBigInteger(),
    this.leastSignificantBits.toBigInteger()
)

/**
 * UUID의 구성요소인 `most significant bits` 와 `least significant bits`로 [LongArray]를 빌드합니다.
 */
fun UUID.toLongArray(): LongArray =
    longArrayOf(mostSignificantBits, leastSignificantBits)

/**
 * [LongArray]로 [UUID]를 빌드한다
 * index 0 가 mostSignificantBits, index 1이 leastSignificantBits 를 의미합니다.
 */
fun LongArray.toUUID(): UUID {
    require(this.size >= 2) { "UUID need 2 long value" }
    return UUID(this[0], this[1])
}
