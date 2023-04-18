package io.bluetape4k.utils.times.period.calendars.seekers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.SeekDirection
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.TimeRange
import io.bluetape4k.utils.times.period.calendars.CalendarVisitor
import io.bluetape4k.utils.times.period.calendars.CalendarVisitorFilter
import io.bluetape4k.utils.times.period.ranges.DayRange
import io.bluetape4k.utils.times.period.ranges.MonthRange
import io.bluetape4k.utils.times.period.ranges.YearRange
import io.bluetape4k.utils.times.period.ranges.YearRangeCollection

open class DaySeeker(
    filter: CalendarVisitorFilter = CalendarVisitorFilter(),
    seekDir: SeekDirection = SeekDirection.FORWARD,
    calendar: ITimeCalendar = TimeCalendar.Default,
): CalendarVisitor<CalendarVisitorFilter, DaySeekerContext>(filter, TimeRange.AnyTime, seekDir, calendar) {

    companion object: KLogging()

    open fun findDay(startDay: DayRange, dayCount: Int): DayRange? {
        log.trace { "find day... startDay=$startDay, dayCount=$dayCount" }

        if (dayCount == 0)
            return startDay

        val context = DaySeekerContext(startDay, dayCount)
        var visitDir = seekDirection

        if (dayCount < 0) {
            visitDir = when {
                visitDir.isForward -> SeekDirection.BACKWARD
                else -> SeekDirection.FORWARD
            }
        }

        startDayVisit(startDay, context, visitDir)
        val foundDay = context.foundDay

        log.trace { "Success to find day. startDay=$startDay, dayCount=$dayCount, visitDir=$visitDir, foundDay=$foundDay" }
        return foundDay
    }

    override fun enterYears(years: YearRangeCollection, context: DaySeekerContext): Boolean = context.notFinished

    override fun enterMonths(year: YearRange, context: DaySeekerContext): Boolean = context.notFinished

    override fun enterDays(month: MonthRange, context: DaySeekerContext): Boolean = context.notFinished

    override fun enterHours(day: DayRange, context: DaySeekerContext): Boolean = false

    override fun onVisitDay(day: DayRange, context: DaySeekerContext): Boolean {
        return when {
            context.isFinished -> false
            day.isSamePeriod(context.startDay) -> true
            !isMatchingDay(day, context) -> true
            !checkLimits(day) -> true

            else -> {
                context.processDay(day)

                // context 가 찾기를 완료하면 탐색(Visit)를 중단하도록 합니다.
                !context.isFinished
            }
        }
    }
}