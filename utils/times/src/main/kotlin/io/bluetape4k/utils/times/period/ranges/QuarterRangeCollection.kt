package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.Quarter
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfQuarter
import io.bluetape4k.utils.times.todayZonedDateTime
import java.time.ZonedDateTime

open class QuarterRangeCollection(
    startTime: ZonedDateTime = todayZonedDateTime(),
    quarterCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): QuarterTimeRange(startTime, quarterCount, calendar) {

    constructor(year: Int, quarter: Quarter, quarterCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(startOfQuarter(year, quarter), quarterCount, calendar)

    fun quarterSequence(): Sequence<QuarterRange> = quarterRanges(start, quarterCount, calendar)

    fun quarters(): List<QuarterRange> = quarterSequence().toFastList()
}
