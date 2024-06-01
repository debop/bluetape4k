package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.startOfWeekOfWeekyear
import java.time.ZonedDateTime

open class WeekRangeCollection(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    weekCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): WeekTimeRange(startTime, weekCount, calendar) {

    constructor(weekyear: Int, weekOfWeekyear: Int, weekCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
            : this(startOfWeekOfWeekyear(weekyear, weekOfWeekyear), weekCount, calendar)

    fun weekSequence(): Sequence<WeekRange> = weekRanges(start, weekCount, calendar)

    fun weeks(): List<WeekRange> = weekSequence().toList()
}
