package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.todayZonedDateTime
import java.time.ZonedDateTime

open class DayRangeCollection(
    startTime: ZonedDateTime = todayZonedDateTime(),
    dayCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): DayTimeRange(startTime, dayCount, calendar) {

    fun daySequence(): Sequence<DayRange> = dayRanges(startDayOfStart, dayCount, calendar)

    fun days(): List<DayRange> = daySequence().toList()
}
