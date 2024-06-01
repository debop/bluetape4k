package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.minutes
import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import java.time.ZonedDateTime

open class MinuteRange(
    moment: ZonedDateTime = ZonedDateTime.now(),
    calendar: ITimeCalendar = TimeCalendar.Default,
): MinuteTimeRange(moment, 1, calendar) {

    val year: Int get() = startYear
    val monthOfYear: Int get() = startMonthOfYear
    val dayOfMonth: Int get() = startDayOfMonth
    val hourOfDay: Int get() = startHourOfDay
    val minuteOfHour: Int get() = startMinuteOfHour
    val secondOfMinute: Int get() = startSecondOfMinute

    fun addMinutes(increment: Int): MinuteRange {
        return MinuteRange(start + increment.minutes(), calendar)
    }

    fun prevMinute(): MinuteRange = addMinutes(-1)
    fun nextMinute(): MinuteRange = addMinutes(1)
}
