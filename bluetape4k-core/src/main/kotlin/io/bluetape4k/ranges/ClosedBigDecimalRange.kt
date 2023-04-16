package io.bluetape4k.ranges

import io.bluetape4k.support.hashOf
import java.math.BigDecimal

internal class ClosedBigDecimalRange(
    override val start: BigDecimal,
    override val endInclusive: BigDecimal,
): ClosedBigNumberRange<BigDecimal> {

    override fun lessThanOrEquals(a: BigDecimal, b: BigDecimal): Boolean = a <= b

    override fun contains(value: BigDecimal): Boolean = value >= start && value <= endInclusive

    override fun isEmpty(): Boolean = start > endInclusive

    override fun equals(other: Any?): Boolean {
        return other is ClosedBigDecimalRange &&
            (isEmpty() && other.isEmpty() || (start == other.start && endInclusive == other.endInclusive))
    }

    override fun hashCode(): Int = hashOf(start, endInclusive)

    override fun toString(): String = "$start..$endInclusive"

}

operator fun BigDecimal.rangeTo(endInclusive: BigDecimal): ClosedBigNumberRange<BigDecimal> =
    ClosedBigDecimalRange(this, endInclusive)
