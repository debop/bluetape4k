package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.period.relativeMonthPeriod
import io.bluetape4k.times.todayZonedDateTime
import java.time.ZonedDateTime

open class MonthTimeRange @JvmOverloads constructor(
    startTime: ZonedDateTime = todayZonedDateTime(),
    val monthCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(startTime.relativeMonthPeriod(monthCount), calendar) {

    fun daySequence(): Sequence<DayRange> = dayRanges(start, end, calendar)

    fun days(): List<DayRange> = daySequence().toList()

    fun hourSequence(): Sequence<HourRange> =
        daySequence().flatMap { it.hourSequence() }

    fun hours(): List<HourRange> = hourSequence().toList()
}
