package io.bluetape4k.ranges

import io.bluetape4k.ranges.impl.DefaultClosedClosedRange
import java.math.BigDecimal
import java.math.BigInteger

fun <T: Comparable<T>> Iterable<ClosedRange<T>>.isAscending(): Boolean {
    val first = firstOrNull() ?: return true

    var max = first.start
    return drop(1).fold(true) { isAscending, elem ->
        val newAscending = isAscending && (max <= elem.start)
        max = maxOf(max, elem.start)
        newAscending
    }
}

@SinceKotlin("1.1")
operator fun BigDecimal.rangeTo(endInclusive: BigDecimal): ClosedClosedRange<BigDecimal> =
    DefaultClosedClosedRange(this, endInclusive)

@SinceKotlin("1.1")
operator fun BigInteger.rangeTo(endInclusive: BigInteger): ClosedClosedRange<BigInteger> =
    DefaultClosedClosedRange(this, endInclusive)
