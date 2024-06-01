package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.todayZonedDateTime
import io.bluetape4k.times.zonedDateTimeOf
import java.time.YearMonth
import java.time.ZonedDateTime

open class MonthRange(
    startTime: ZonedDateTime = todayZonedDateTime(),
    calendar: ITimeCalendar = TimeCalendar.Default,
): MonthTimeRange(startTime, 1, calendar) {

    constructor(year: Int, monthOfYear: Int, calendar: ITimeCalendar = TimeCalendar.Default)
            : this(zonedDateTimeOf(year, monthOfYear), calendar)

    constructor(yearMonth: YearMonth, calendar: ITimeCalendar = TimeCalendar.Default)
            : this(zonedDateTimeOf(yearMonth.year, yearMonth.monthValue), calendar)

    val year: Int get() = startYear
    val monthOfYear: Int get() = startMonthOfYear

    fun addMonths(months: Int): MonthRange = MonthRange(start.plusMonths(months.toLong()), calendar)

    fun prevMonth(): MonthRange = addMonths(-1)
    fun nextMonth(): MonthRange = addMonths(1)
}
