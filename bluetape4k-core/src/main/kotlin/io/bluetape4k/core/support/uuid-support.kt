package io.bluetape4k.core.support

import io.bluetape4k.core.utils.BigIntegerPair
import java.math.BigInteger
import java.util.*

fun BigInteger.toUuid(): UUID {
    val unpaired = BigIntegerPair.unpair(this)
    return UUID(unpaired[0].longValueExact(), unpaired[1].longValueExact())
}

fun UUID.toBigInt(): BigInteger =
    BigIntegerPair.pair(
        this.mostSignificantBits.toBigInteger(),
        this.leastSignificantBits.toBigInteger()
    )
