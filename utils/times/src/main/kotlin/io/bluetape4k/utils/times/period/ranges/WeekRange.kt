package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfWeekOfWeekyear
import java.time.ZonedDateTime

open class WeekRange(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    calendar: ITimeCalendar = TimeCalendar.Default,
) : WeekTimeRange(startTime, 1, calendar) {

    constructor(weekyear: Int, weekOfWeekyear: Int, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(startOfWeekOfWeekyear(weekyear, weekOfWeekyear), calendar)

    val firstDayOfWeek: ZonedDateTime get() = start
    val lastDayOfWeek: ZonedDateTime get() = end

    fun isMultipleCalendarYears(): Boolean = calendar.year(firstDayOfWeek) != calendar.year(lastDayOfWeek)

    fun addWeeks(weeks: Int): WeekRange = WeekRange(start.plusWeeks(weeks.toLong()), calendar)
    fun addWeeks(weeks: Long): WeekRange = WeekRange(start.plusWeeks(weeks), calendar)

    fun prevWeek(): WeekRange = addWeeks(-1)
    fun nextWeek(): WeekRange = addWeeks(1)
}
