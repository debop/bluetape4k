package io.bluetape4k.times.period.ranges

import io.bluetape4k.times.period.ITimeCalendar
import io.bluetape4k.times.period.TimeCalendar
import java.time.ZonedDateTime

open class MinuteRangeCollection(
    startTime: ZonedDateTime = ZonedDateTime.now(),
    minuteCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): MinuteTimeRange(startTime, minuteCount, calendar) {

    fun minuteSequence(): Sequence<MinuteRange> =
        minuteRanges(startMinuteOfStart, minuteCount, calendar)

    fun minutes(): List<MinuteRange> = minuteSequence().toList()
}
