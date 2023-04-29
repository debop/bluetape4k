package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.todayZonedDateTime
import io.bluetape4k.utils.times.zonedDateTimeOf
import java.time.ZonedDateTime

open class MonthRangeCollection(
    startTime: ZonedDateTime = todayZonedDateTime(),
    monthCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): MonthTimeRange(startTime, monthCount, calendar) {

    constructor(year: Int, monthOfYear: Int, monthCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(zonedDateTimeOf(year, monthOfYear), monthCount, calendar)

    fun monthSequence(): Sequence<MonthRange> = monthRanges(startDayOfStart, monthCount, calendar)

    fun months(): List<MonthRange> = monthSequence().toFastList()
}
