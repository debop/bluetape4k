package io.bluetape4k.utils.times.interval

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.areEquals
import io.bluetape4k.support.hashOf
import io.bluetape4k.utils.times.TimeSpec.SystemZoneId
import io.bluetape4k.utils.times.interval.ReadableTemporalInterval.Companion.SEPARATOR
import io.bluetape4k.utils.times.toEpochMillis
import java.time.ZoneId
import java.time.temporal.Temporal

/**
 * JodaTime's AbstractInterval class 를 참고하여 구현하였습니다.
 */
abstract class AbstractTemporalInterval<T> : ReadableTemporalInterval<T> where T : Temporal, T : Comparable<T> {

    companion object : KLogging()

    override val zoneId: ZoneId
        get() = SystemZoneId

    /**
     * 두 Interval이 연속해 있으면 true를 아니면 false 를 반환한다.
     * @param other
     */
    override fun abuts(other: ReadableTemporalInterval<T>): Boolean =
        areEquals(startInclusive, other.endExclusive) || areEquals(endExclusive, other.startInclusive)

    override fun gap(interval: ReadableTemporalInterval<T>): ReadableTemporalInterval<T>? {
        return when {
            overlaps(interval) -> null
            else               -> temporalIntervalOf(
                maxOf(startInclusive, interval.startInclusive),
                minOf(endExclusive, interval.endExclusive),
                zoneId
            )
        }
    }

    override fun overlap(interval: ReadableTemporalInterval<T>): ReadableTemporalInterval<T>? {
        return when {
            overlaps(interval) -> temporalIntervalOf(
                maxOf(startInclusive, interval.startInclusive),
                minOf(endExclusive, interval.endExclusive),
                zoneId
            )

            else -> null
        }
    }

    override fun overlaps(other: ReadableTemporalInterval<T>): Boolean =
        overlaps(other.startInclusive) || overlaps(other.endExclusive)

    /**
     * the specific moment in <code>[start, end]</code>
     * @param moment
     */
    override fun overlaps(moment: T): Boolean = moment >= startInclusive && moment < endExclusive

    /**
     * given interval is inner inverval of this interval
     * @param other
     */
    override operator fun contains(other: ReadableTemporalInterval<T>): Boolean =
        contains(other.startInclusive) && contains(other.endExclusive)

    override fun contains(epochMillis: Long): Boolean =
        epochMillis >= startInclusive.toEpochMillis() && epochMillis < endExclusive.toEpochMillis()

    /**
     * 현 Interval 이 `other` interval 보다 이전인가?
     * @param other
     */
    override fun isBefore(other: ReadableTemporalInterval<T>): Boolean = endExclusive < other.endExclusive

    /**
     * This interval is before to given instant
     * @param moment 시각
     */
    override fun isBefore(moment: T): Boolean = endExclusive < moment

    override fun isAfter(other: ReadableTemporalInterval<T>): Boolean = startInclusive >= other.startInclusive

    override fun isAfter(moment: T): Boolean = moment < startInclusive

    override fun withStart(newStart: T): ReadableTemporalInterval<T> = when {
        newStart > this.endExclusive -> temporalIntervalOf(this.endExclusive, newStart, zoneId)
        else                         -> temporalIntervalOf(newStart, this.endExclusive, zoneId)
    }

    override fun withEnd(newEnd: T): ReadableTemporalInterval<T> = when {
        newEnd < this.startInclusive -> temporalIntervalOf(newEnd, this.startInclusive, zoneId)
        else                         -> temporalIntervalOf(this.startInclusive, newEnd, zoneId)
    }


    override fun compareTo(other: ClosedRange<T>): Int =
        startInclusive.compareTo(other.start)

    override fun equals(other: Any?): Boolean =
        other is ReadableTemporalInterval<*> &&
        areEquals(startInclusive, other.startInclusive) &&
        areEquals(endExclusive, other.endExclusive) &&
        areEquals(zoneId, other.zoneId)

    override fun hashCode(): Int = hashOf(startInclusive, endExclusive, zoneId)

    override fun toString(): String = "$startInclusive $SEPARATOR $endExclusive"
}
