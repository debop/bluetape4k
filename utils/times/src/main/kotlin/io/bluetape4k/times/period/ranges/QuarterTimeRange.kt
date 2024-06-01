package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.Quarter
import io.bluetape4k.times.TimeSpec.MonthsPerQuarter
import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.period.relativeQuarterPeriod
import io.bluetape4k.times.todayZonedDateTime
import java.time.ZonedDateTime

open class QuarterTimeRange(
    startTime: ZonedDateTime = todayZonedDateTime(),
    val quarterCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(startTime.relativeQuarterPeriod(quarterCount), calendar) {

    val quarterOfStart: Quarter get() = Quarter.ofMonth(startMonthOfYear)
    val quarterOfEnd: Quarter get() = Quarter.ofMonth(endMonthOfYear)

    val isMultipleCalendarYears: Boolean = startYear != endYear

    val isMultipleCalendarQuarters: Boolean = isMultipleCalendarYears || quarterOfStart != quarterOfEnd


    fun monthSequence(): Sequence<MonthRange> =
        monthRanges(startMonthOfStart, quarterCount * MonthsPerQuarter, calendar)

    fun months(): List<MonthRange> = monthSequence().toList()

    fun daySequence(): Sequence<DayRange> = monthSequence().flatMap { it.daySequence() }

    fun days(): List<DayRange> = daySequence().toList()
}
