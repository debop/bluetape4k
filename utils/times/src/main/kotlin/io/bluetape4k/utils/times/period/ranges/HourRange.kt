package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfHour
import java.time.ZonedDateTime

open class HourRange(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    calendar: ITimeCalendar = TimeCalendar.Default,
): HourTimeRange(startTime, 1, calendar) {

    val year: Int get() = startYear
    val monthOfYear: Int get() = startMonthOfYear
    val dayOfMonth: Int get() = startDayOfMonth
    val hourOfDay: Int get() = startHourOfDay

    fun addHours(hours: Int): HourRange {
        val startHour = this.start.startOfHour()
        return HourRange(startHour.plusHours(hours.toLong()), calendar)
    }

    fun prevHour(): HourRange = addHours(-1)
    fun nextHour(): HourRange = addHours(1)
}
