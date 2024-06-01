package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.zonedDateTimeOf
import java.time.ZonedDateTime

open class HourRangeCollection(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    hourCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): HourTimeRange(startTime, hourCount, calendar) {

    constructor(
        year: Int,
        monthOfYear: Int = 1,
        dayOfMonth: Int = 1,
        hourOfDay: Int = 0,
        hourCount: Int = 1,
        calendar: ITimeCalendar = TimeCalendar.Default,
    ): this(zonedDateTimeOf(year, monthOfYear, dayOfMonth, hourOfDay), hourCount, calendar)

    fun hourSequence(): Sequence<HourRange> = hourRanges(startHourOfStart, hourCount, calendar)

    fun hours(): List<HourRange> = hourSequence().toList()

}
