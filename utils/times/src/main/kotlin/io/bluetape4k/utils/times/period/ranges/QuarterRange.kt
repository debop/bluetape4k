package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.Quarter
import io.bluetape4k.utils.times.TimeSpec.MonthsPerQuarter
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfQuarter
import io.bluetape4k.utils.times.todayZonedDateTime
import java.time.ZonedDateTime

open class QuarterRange(
    startTime: ZonedDateTime = todayZonedDateTime(),
    calendar: ITimeCalendar = TimeCalendar.Default,
): QuarterTimeRange(startTime, 1, calendar) {

    constructor(year: Int, quarter: Quarter, calendar: ITimeCalendar = TimeCalendar.Default)
        : this(startOfQuarter(year, quarter), calendar)

    val year: Int get() = startYear
    val quarter: Quarter get() = quarterOfStart

    fun addQuarters(quarters: Int): QuarterRange =
        QuarterRange(start.plusMonths(quarters.toLong() * MonthsPerQuarter), calendar)

    fun prevQuarter(): QuarterRange = addQuarters(-1)
    fun nextQuarter(): QuarterRange = addQuarters(1)
}
