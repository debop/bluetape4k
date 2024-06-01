package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.ITimePeriod
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.period.TimePeriod

open class YearCalendarTimeRange(
    period: ITimePeriod = TimePeriod.AnyTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(period, calendar) {

    private val baseMonth: Int = 1

    val baseYear: Int get() = startYear
}
