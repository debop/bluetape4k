package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.todayZonedDateTime
import io.bluetape4k.utils.times.zonedDateTimeOf
import java.time.DayOfWeek
import java.time.ZonedDateTime

open class DayRange(
    startTime: ZonedDateTime = todayZonedDateTime(),
    calendar: ITimeCalendar = TimeCalendar.Default,
) : DayTimeRange(startTime, 1, calendar) {

    constructor(year: Int, monthOfYear: Int, dayOfMonth: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(zonedDateTimeOf(year, monthOfYear, dayOfMonth), calendar)

    val year: Int get() = startYear
    val monthOfYear: Int get() = startMonthOfYear
    val dayOfMonth: Int get() = startDayOfMonth
    val dayOfWeek: DayOfWeek get() = startDayOfWeek

    fun addDays(days: Int): DayRange = DayRange(start.plusDays(days.toLong()), calendar)

    fun prevDay(): DayRange = addDays(-1)
    fun nextDay(): DayRange = addDays(1)
}
