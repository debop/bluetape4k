package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.todayZonedDateTime
import java.time.ZonedDateTime

open class DayRangeCollection(
    startTime: ZonedDateTime = todayZonedDateTime(),
    dayCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
) : DayTimeRange(startTime, dayCount, calendar) {

    fun daySequence(): Sequence<DayRange> = dayRanges(startDayOfStart, dayCount, calendar)

    fun days(): List<DayRange> = daySequence().toFastList()
}
