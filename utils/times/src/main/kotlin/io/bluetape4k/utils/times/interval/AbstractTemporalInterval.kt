package io.bluetape4k.utils.times.interval

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.areEquals
import io.bluetape4k.support.hashOf
import io.bluetape4k.utils.times.SystemZoneId
import io.bluetape4k.utils.times.interval.ReadableTemporalInterval.Companion.SEPARATOR
import io.bluetape4k.utils.times.toEpochMillis
import java.time.ZoneId
import java.time.temporal.Temporal

/**
 * JodaTime's AbstractInterval class 를 참고하여 구현하였습니다.
 */
abstract class AbstractTemporalInterval<T>: ReadableTemporalInterval<T> where T: Temporal, T: Comparable<T> {

    companion object: KLogging()

    override val zoneId: ZoneId
        get() = SystemZoneId

    /**
     * 두 Interval이 연속해 있으면 true를 아니면 false 를 반환한다.
     * @param other
     */
    override fun abuts(other: ReadableTemporalInterval<T>): Boolean =
        areEquals(start, other.end) || areEquals(end, other.start)

    override fun gap(interval: ReadableTemporalInterval<T>): ReadableTemporalInterval<T>? {
        return when {
            overlaps(interval) -> null
            else -> temporalIntervalOf(maxOf(start, interval.start), minOf(end, interval.end), zoneId)
        }
    }

    override fun overlap(interval: ReadableTemporalInterval<T>): ReadableTemporalInterval<T>? {
        return when {
            overlaps(interval) -> temporalIntervalOf(maxOf(start, interval.start), minOf(end, interval.end), zoneId)
            else -> null
        }
    }

    override fun overlaps(other: ReadableTemporalInterval<T>): Boolean =
        overlaps(other.start) || overlaps(other.end)

    /**
     * the specific moment in <code>[start, end]</code>
     * @param moment
     */
    override fun overlaps(moment: T): Boolean = moment >= start && moment < end

    /**
     * given interval is inner inverval of this interval
     * @param other
     */
    override operator fun contains(other: ReadableTemporalInterval<T>): Boolean =
        contains(other.start) && contains(other.end)

    override fun contains(epochMillis: Long): Boolean =
        epochMillis >= start.toEpochMillis() && epochMillis < end.toEpochMillis()

    /**
     * 현 Interval 이 `other` interval 보다 이전인가?
     * @param other
     */
    override fun isBefore(other: ReadableTemporalInterval<T>): Boolean = end < other.end

    /**
     * This interval is before to given instant
     * @param instant
     */
    override fun isBefore(moment: T): Boolean = end < moment

    override fun isAfter(other: ReadableTemporalInterval<T>): Boolean = start >= other.start

    override fun isAfter(moment: T): Boolean = moment < start

    override fun withStart(newStart: T): ReadableTemporalInterval<T> = when {
        newStart > this.end -> temporalIntervalOf(this.end, newStart, zoneId)
        else -> temporalIntervalOf(newStart, this.end, zoneId)
    }

    override fun withEnd(newEnd: T): ReadableTemporalInterval<T> = when {
        newEnd < this.start -> temporalIntervalOf(newEnd, this.start, zoneId)
        else -> temporalIntervalOf(this.start, newEnd, zoneId)
    }


    override fun compareTo(other: ClosedRange<T>): Int =
        start.compareTo(other.start)

    override fun equals(other: Any?): Boolean =
        other is ReadableTemporalInterval<*> &&
            areEquals(start, other.start) &&
            areEquals(end, other.end) &&
            areEquals(zoneId, other.zoneId)

    override fun hashCode(): Int = hashOf(start, end, zoneId)

    override fun toString(): String = "$start $SEPARATOR $end"
}
