package io.bluetape4k.ahocorasick.interval

import io.bluetape4k.core.AbstractValueObject
import java.util.Objects

open class Interval(
    override val start: Int,
    override val end: Int,
): AbstractValueObject(), Intervalable {

    companion object {
        val EMPTY = Interval(1, 0)
    }

    val isEmpty: Boolean get() = start > end

    fun overlapsWith(other: Interval): Boolean =
        start < other.end && end >= other.start

    fun overlapsWith(point: Int): Boolean =
        point in start..end

    override fun compareTo(other: Intervalable): Int {
        var comparison = start - other.start
        if (comparison == 0) {
            comparison = end - other.end
        }
        return comparison
    }

    override fun equalProperties(other: Any): Boolean {
        return (other is Intervalable) && start == other.start && end == other.end
    }

    override fun equals(other: Any?): Boolean {
        return other != null && equalProperties(other)
    }

    override fun hashCode(): Int = if (isEmpty) -1 else Objects.hash(start, end)

    override fun toString(): String = "$start:$end"
}
