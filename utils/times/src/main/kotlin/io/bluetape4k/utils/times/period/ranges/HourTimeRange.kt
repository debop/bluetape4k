package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.TimeSpec.MinutesPerHour
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.relativeHourPeriod
import java.time.ZonedDateTime

open class HourTimeRange(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    val hourCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
) : CalendarTimeRange(startTime.relativeHourPeriod(hourCount), calendar) {

    val hourOfDayOfEnd: Int get() = end.hour

    fun minuteSequence(): Sequence<MinuteRange> =
        minuteRanges(startMinuteOfStart, hourCount * MinutesPerHour, calendar)

    fun minutes(): List<MinuteRange> = minuteSequence().toFastList()
}
