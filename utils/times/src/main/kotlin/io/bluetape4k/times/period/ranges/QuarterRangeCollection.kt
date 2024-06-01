package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.Quarter
import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import io.bluetape4k.times.startOfQuarter
import io.bluetape4k.times.todayZonedDateTime
import java.time.ZonedDateTime

open class QuarterRangeCollection(
    startTime: ZonedDateTime = todayZonedDateTime(),
    quarterCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): QuarterTimeRange(startTime, quarterCount, calendar) {

    constructor(year: Int, quarter: Quarter, quarterCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
            : this(startOfQuarter(year, quarter), quarterCount, calendar)

    fun quarterSequence(): Sequence<QuarterRange> = quarterRanges(start, quarterCount, calendar)

    fun quarters(): List<QuarterRange> = quarterSequence().toList()
}
