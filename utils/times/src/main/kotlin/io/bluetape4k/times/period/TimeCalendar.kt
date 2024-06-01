package io.bluetape4k.times.period

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.logging.KLogging
import io.bluetape4k.times.TimeSpec.MaxPeriodTime
import io.bluetape4k.times.TimeSpec.MinPeriodTime
import java.time.DayOfWeek
import java.time.Duration
import java.time.ZonedDateTime

open class TimeCalendar @JvmOverloads constructor(
    private val config: TimeCalendarConfig = TimeCalendarConfig.Default,
): AbstractValueObject(), ITimeCalendar {

    companion object: KLogging() {
        @JvmStatic
        val Default: TimeCalendar = TimeCalendar(TimeCalendarConfig.Default)

        @JvmStatic
        val EmptyOffset: TimeCalendar = TimeCalendar(TimeCalendarConfig.EmptyOffset)

        @JvmOverloads
        @JvmStatic
        fun of(cfg: TimeCalendarConfig = TimeCalendarConfig.Default): TimeCalendar = TimeCalendar(cfg)
    }

    override val startOffset: Duration get() = config.startOffset
    override val endOffset: Duration get() = config.endOffset
    override val firstDayOfWeek: DayOfWeek get() = config.firstDayOfWeek

    override fun mapStart(moment: ZonedDateTime): ZonedDateTime = when {
        moment > MinPeriodTime -> moment + startOffset
        else                   -> moment
    }

    override fun mapEnd(moment: ZonedDateTime): ZonedDateTime = when {
        moment < MaxPeriodTime -> moment + endOffset
        else                   -> moment
    }

    override fun unmapStart(moment: ZonedDateTime): ZonedDateTime = when {
        moment > MinPeriodTime -> moment - startOffset
        else                   -> moment
    }

    override fun unmapEnd(moment: ZonedDateTime): ZonedDateTime = when {
        moment < MaxPeriodTime -> moment - endOffset
        else                   -> moment
    }

    override fun equalProperties(other: Any): Boolean {
        return other is ITimeCalendar &&
                startOffset == other.startOffset &&
                endOffset == other.endOffset &&
                firstDayOfWeek == other.firstDayOfWeek
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("startOffset", startOffset)
            .add("endOffset", endOffset)
            .add("firstDayOfWeek", firstDayOfWeek)
    }
}
