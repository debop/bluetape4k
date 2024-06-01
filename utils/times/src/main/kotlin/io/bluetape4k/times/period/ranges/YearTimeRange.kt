package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.TimeSpec.MonthsPerYear
import io.bluetape4k.times.TimeSpec.QuartersPerYear
import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.period.relativeYearPeriodOf

open class YearTimeRange(
    val year: Int,
    val yearCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): YearCalendarTimeRange(relativeYearPeriodOf(year, yearCount), calendar) {

    fun quarterSequence(): Sequence<QuarterRange> =
        quarterRanges(start, yearCount * QuartersPerYear, calendar)

    fun quarters(): List<QuarterRange> = quarterSequence().toList()

    fun monthSequence(): Sequence<MonthRange> =
        monthRanges(start, yearCount * MonthsPerYear, calendar)

    fun months(): List<MonthRange> = monthSequence().toList()

    fun daySequence(): Sequence<DayRange> = monthSequence().flatMap { it.daySequence() }

    fun days(): List<DayRange> = daySequence().toList()

    fun hourSequence(): Sequence<HourRange> = daySequence().flatMap { it.hourSequence() }

    fun hours(): List<HourRange> = hourSequence().toList()

    fun minuteSequence(): Sequence<MinuteRange> = hourSequence().flatMap { it.minuteSequence() }

    fun minutes(): List<MinuteRange> = minuteSequence().toList()
}
