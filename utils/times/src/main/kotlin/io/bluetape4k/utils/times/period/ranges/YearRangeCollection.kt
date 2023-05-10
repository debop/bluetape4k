package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import java.time.ZonedDateTime

open class YearRangeCollection protected constructor(
    year: Int,
    yearCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): YearTimeRange(year, yearCount, calendar) {

    companion object {
        @JvmStatic
        operator fun invoke(
            year: Int,
            yearCount: Int,
            calendar: ITimeCalendar = TimeCalendar.Default,
        ): YearRangeCollection {
            return YearRangeCollection(year, yearCount, calendar)
        }

        @JvmStatic
        operator fun invoke(
            time: ZonedDateTime,
            yearCount: Int,
            calendar: ITimeCalendar = TimeCalendar.Default,
        ): YearRangeCollection {
            return invoke(time.year, yearCount, calendar)
        }
    }

    fun yearSequence(): Sequence<YearRange> = yearRanges(year, yearCount, calendar)

    fun years(): List<YearRange> = yearSequence().toFastList()
}
