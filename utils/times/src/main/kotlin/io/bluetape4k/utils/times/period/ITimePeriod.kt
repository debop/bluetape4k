package io.bluetape4k.utils.times.period

import io.bluetape4k.core.ValueObject
import io.bluetape4k.utils.times.TimeSpec.EmptyDuration
import io.bluetape4k.utils.times.TimeSpec.MaxPeriodTime
import io.bluetape4k.utils.times.TimeSpec.MinPeriodTime
import java.time.Duration
import java.time.ZonedDateTime

/**
 * Time period 를 나타내는 인터페이스
 */
interface ITimePeriod : ValueObject, Comparable<ITimePeriod> {

    val start: ZonedDateTime

    val end: ZonedDateTime

    val readonly: Boolean

    val duration: Duration
        get() = when {
            hasPeriod -> Duration.between(start, end)
            else      -> EmptyDuration
        }

    val hasStart: Boolean
        get() = start != MinPeriodTime

    val hasEnd: Boolean
        get() = end != MaxPeriodTime

    val hasPeriod: Boolean
        get() = hasStart && hasEnd

    val isMoment: Boolean
        get() = start == end

    val isAnyTime: Boolean
        get() = !hasStart && !hasEnd

    fun setup(newStart: ZonedDateTime? = MinPeriodTime, newEnd: ZonedDateTime? = MaxPeriodTime)

    fun copy(offset: Duration = Duration.ZERO): ITimePeriod

    fun move(offset: Duration = Duration.ZERO)

    fun isSamePeriod(other: ITimePeriod?): Boolean

    fun reset()
}
