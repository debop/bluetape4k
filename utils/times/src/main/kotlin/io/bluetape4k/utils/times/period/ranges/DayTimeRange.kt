package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.utils.times.HoursPerDay
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.relativeDayPeriod
import io.bluetape4k.utils.times.todayZonedDateTime
import java.time.ZonedDateTime

open class DayTimeRange(
    startTime: ZonedDateTime = todayZonedDateTime(),
    val dayCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarTimeRange(startTime.relativeDayPeriod(dayCount), calendar) {

    fun hourSequence(): Sequence<HourRange> =
        hourRanges(startDayOfStart, dayCount * HoursPerDay, calendar)

    fun hours(): List<HourRange> = hourSequence().toList()

    fun minuteSequence(): Sequence<MinuteRange> =
        hourSequence().flatMap { it.minuteSequence() }

    fun minutes(): List<MinuteRange> = minuteSequence().toFastList()
}
