package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.DaysPerWeek
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.relativeWeekPeriod
import io.bluetape4k.utils.times.weekOfWeekyear
import io.bluetape4k.utils.times.weekyear
import java.time.ZonedDateTime

open class WeekTimeRange(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    val weekCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(startTime.relativeWeekPeriod(weekCount), calendar) {

    val year: Int get() = startYear
    val weekyear: Int get() = start.weekyear
    val weekOfWeekyear: Int get() = start.weekOfWeekyear

    val startWeekyear: Int get() = start.weekyear
    val startWeekOfWeekyear: Int get() = start.weekOfWeekyear
    val endWeekyear: Int get() = end.weekyear
    val endWeekOfWeekyear: Int get() = end.weekOfWeekyear

    fun daySequence(): Sequence<DayRange> =
        dayRanges(startDayOfStart, weekCount * DaysPerWeek, calendar)

    fun days(): List<DayRange> = daySequence().toList()
}
