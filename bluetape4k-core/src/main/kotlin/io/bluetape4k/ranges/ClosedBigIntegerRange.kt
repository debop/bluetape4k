package io.bluetape4k.ranges

import io.bluetape4k.support.hashOf
import java.math.BigInteger

internal class ClosedBigIntegerRange(
    override val start: BigInteger,
    override val endInclusive: BigInteger,
): ClosedBigNumberRange<BigInteger> {

    override fun lessThanOrEquals(a: BigInteger, b: BigInteger): Boolean = a <= b

    override fun contains(value: BigInteger): Boolean = value >= start && value <= endInclusive

    override fun isEmpty(): Boolean = start > endInclusive

    override fun equals(other: Any?): Boolean {
        return other is ClosedBigIntegerRange &&
            (isEmpty() && other.isEmpty() || (start == other.start && endInclusive == other.endInclusive))
    }

    override fun hashCode(): Int = hashOf(start, endInclusive)

    override fun toString(): String = "$start..$endInclusive"

}

operator fun BigInteger.rangeTo(endInclusive: BigInteger): ClosedBigNumberRange<BigInteger> =
    ClosedBigIntegerRange(this, endInclusive)
