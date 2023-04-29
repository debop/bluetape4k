package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfWeekOfWeekyear
import java.time.ZonedDateTime

open class WeekRangeCollection(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    weekCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): WeekTimeRange(startTime, weekCount, calendar) {

    @JvmOverloads
    constructor(weekyear: Int, weekOfWeekyear: Int, weekCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(startOfWeekOfWeekyear(weekyear, weekOfWeekyear), weekCount, calendar)

    fun weekSequence(): Sequence<WeekRange> = weekRanges(start, weekCount, calendar)

    fun weeks(): List<WeekRange> = weekSequence().toFastList()
}
