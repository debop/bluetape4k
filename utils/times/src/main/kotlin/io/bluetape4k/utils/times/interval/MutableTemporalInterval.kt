package io.bluetape4k.utils.times.interval

import io.bluetape4k.utils.times.UtcZoneId
import java.time.ZoneId
import java.time.temporal.Temporal

/**
 * Mutable [TemporalInterval]
 */
class MutableTemporalInterval<T>(
    start: T,
    end: T,
    override val zoneId: ZoneId = UtcZoneId,
): AbstractTemporalInterval<T>() where T: Temporal, T: Comparable<T> {

    constructor(other: ReadableTemporalInterval<T>): this(other.startInclusive, other.endExclusive, other.zoneId)

    override var startInclusive: T = start
        set(value) {
            if (value > endExclusive) {
                field = endExclusive
                endExclusive = value
            } else {
                field = value
            }
        }

    override var endExclusive: T = end
        set(value) {
            if (value < startInclusive) {
                field = startInclusive
                this.startInclusive = value
            } else {
                field = value
            }
        }

    override fun withStart(newStart: T): MutableTemporalInterval<T> = when {
        newStart < endExclusive -> mutableTemporalIntervalOf(newStart, this.endExclusive, zoneId)
        else -> mutableTemporalIntervalOf(endExclusive, newStart, zoneId)
    }

    override fun withEnd(newEnd: T): ReadableTemporalInterval<T> = when {
        newEnd > startInclusive -> mutableTemporalIntervalOf(this.startInclusive, newEnd, zoneId)
        else -> mutableTemporalIntervalOf(newEnd, this.startInclusive, zoneId)
    }

}