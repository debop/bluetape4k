package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.relativeMinutePeriod
import java.time.ZonedDateTime

open class MinuteTimeRange(
    moment: ZonedDateTime = ZonedDateTime.now(),
    val minuteCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(moment.relativeMinutePeriod(minuteCount), calendar) {

    val minuteOfHourOfEnd: Int get() = end.minute
}
