package io.bluetape4k.utils.times.period.ranges.coroutines

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.ranges.DayRange
import io.bluetape4k.utils.times.period.ranges.HourRange
import io.bluetape4k.utils.times.period.ranges.MinuteRange
import io.bluetape4k.utils.times.period.ranges.MonthRange
import io.bluetape4k.utils.times.period.ranges.WeekRange
import io.bluetape4k.utils.times.period.ranges.YearRange
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun flowOfYearRange(
    startTime: ZonedDateTime,
    yearCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<YearRange> {
    yearCount.assertPositiveNumber("yearCount")

    return flow {
        val start = YearRange(startTime, calendar)
        var years = 0
        while (years < yearCount) {
            emit(start.addYears(years))
            years++
        }
    }
}

fun flowOfMonthRange(
    startTime: ZonedDateTime,
    monthCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<MonthRange> {
    monthCount.assertPositiveNumber("monthCount")

    return flow {
        val start = MonthRange(startTime, calendar)
        var months = 0
        while (months < monthCount) {
            emit(start.addMonths(months))
            months++
        }
    }
}

fun flowOfWeekRange(
    startTime: ZonedDateTime,
    weekCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<WeekRange> {
    weekCount.assertPositiveNumber("weekCount")

    return flow {
        val start = WeekRange(startTime, calendar)
        var weeks = 0
        while (weeks < weekCount) {
            emit(start.addWeeks(weeks))
            weeks++
        }
    }
}

fun flowOfDayRange(
    startTime: ZonedDateTime,
    dayCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<DayRange> {
    dayCount.assertPositiveNumber("dayCount")

    return flow {
        val start = DayRange(startTime, calendar)
        var days = 0
        while (days < dayCount) {
            emit(start.addDays(days))
            days++
        }
    }
}

fun flowOfHourRange(
    startTime: ZonedDateTime,
    hourCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<HourRange> {
    hourCount.assertPositiveNumber("hourCount")

    return flow {
        val start = HourRange(startTime, calendar)
        var hours = 0
        while (hours < hourCount) {
            emit(start.addHours(hours))
            hours++
        }
    }
}

fun flowOfMinuteRange(
    startTime: ZonedDateTime,
    minuteCount: Int,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Flow<MinuteRange> {
    minuteCount.assertPositiveNumber("minuteCount")

    return flow {
        val start = MinuteRange(startTime, calendar)
        var minutes = 0
        while (minutes < minuteCount) {
            emit(start.addMinutes(minutes))
            minutes++
        }
    }
}
