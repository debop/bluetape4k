package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.nowZonedDateTime
import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import java.time.ZonedDateTime

open class YearRange(
    year: Int = nowZonedDateTime().year,
    calendar: ITimeCalendar = TimeCalendar.Default,
): YearTimeRange(year, 1, calendar) {

    constructor(
        moment: ZonedDateTime,
        calendar: ITimeCalendar = TimeCalendar.Default,
    ): this(moment.year, calendar)

    fun addYears(years: Int): YearRange = YearRange(year + years, calendar)

    fun prevYear(): YearRange = addYears(-1)
    fun nextYear(): YearRange = addYears(1)
}
