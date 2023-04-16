package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.TimePeriod

open class YearCalendarTimeRange(
    period: ITimePeriod = TimePeriod.AnyTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(period, calendar) {

    private val baseMonth: Int = 1

    val baseYear: Int get() = startYear
}
