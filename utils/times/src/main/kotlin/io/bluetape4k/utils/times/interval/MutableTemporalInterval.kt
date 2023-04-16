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

    constructor(other: ReadableTemporalInterval<T>): this(other.start, other.end, other.zoneId)

    override var start: T = start
        set(value) {
            if (value > end) {
                field = end
                end = value
            } else {
                field = value
            }
        }

    override var end: T = end
        set(value) {
            if (value < start) {
                field = start
                this.start = value
            } else {
                field = value
            }
        }

    override fun withStart(newStart: T): MutableTemporalInterval<T> = when {
        newStart < end -> mutableTemporalIntervalOf(newStart, this.end, zoneId)
        else -> mutableTemporalIntervalOf(end, newStart, zoneId)
    }

    override fun withEnd(newEnd: T): ReadableTemporalInterval<T> = when {
        newEnd > start -> mutableTemporalIntervalOf(this.start, newEnd, zoneId)
        else -> mutableTemporalIntervalOf(newEnd, this.start, zoneId)
    }

}
