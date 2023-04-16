package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.utils.times.days
import io.bluetape4k.utils.times.hours
import io.bluetape4k.utils.times.minutes
import io.bluetape4k.utils.times.monthPeriod
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.weeks
import java.time.ZonedDateTime


private val log = KotlinLogging.logger {}

@JvmOverloads
fun yearRanges(year: Int, yearCount: Int = 1, calendar: ITimeCalendar = TimeCalendar.Default): Sequence<YearRange> {
    yearCount.assertPositiveNumber("yearCount")

    return sequence {
        var count = 0
        var current = YearRange(year, calendar)
        while (count < yearCount) {
            yield(current)
            current = current.nextYear()
            count++
        }
    }
}

@JvmOverloads
fun quarterRanges(
    startTime: ZonedDateTime,
    quarterCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<QuarterRange> {
    quarterCount.assertPositiveNumber("quarterCount")

    return sequence {
        var count = 0
        var current = QuarterRange(startTime, calendar)
        while (count < quarterCount) {
            yield(current)
            current = current.nextQuarter()
            count++
        }
    }
}

@JvmOverloads
fun monthRanges(
    startTime: ZonedDateTime,
    monthCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<MonthRange> {
    monthCount.assertPositiveNumber("monthCount")
    return monthRanges(startTime, startTime + monthCount.monthPeriod(), calendar)
}

fun monthRanges(
    start: ZonedDateTime,
    end: ZonedDateTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<MonthRange> = sequence {
    var current = MonthRange(start, calendar)
    while (current.end <= end) {
        yield(current)
        current = current.nextMonth()
    }
}

@JvmOverloads
fun weekRanges(
    start: ZonedDateTime,
    weekCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<WeekRange> {
    weekCount.assertPositiveNumber("weekCount")
    return weekRanges(start, start + weekCount.weeks(), calendar)
}

fun weekRanges(
    start: ZonedDateTime,
    end: ZonedDateTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<WeekRange> = sequence {
    var current = WeekRange(start, calendar)

    while (current.end <= end) {
        yield(current)
        current = current.nextWeek()
    }
}

@JvmOverloads
fun dayRanges(
    start: ZonedDateTime,
    dayCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<DayRange> {
    dayCount.assertPositiveNumber("dayCount")
    return dayRanges(start, start + dayCount.days(), calendar)
}

@JvmOverloads
fun dayRanges(
    start: ZonedDateTime,
    end: ZonedDateTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<DayRange> = sequence {
    var current = DayRange(start, calendar)

    while (current.end <= end) {
        yield(current)
        current = current.nextDay()
    }
}

@JvmOverloads
fun hourRanges(
    start: ZonedDateTime,
    hourCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<HourRange> {
    hourCount.assertPositiveNumber("hourCount")
    return hourRanges(start, start + hourCount.hours(), calendar)
}

fun hourRanges(
    start: ZonedDateTime,
    end: ZonedDateTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<HourRange> = sequence {
    var current = HourRange(start, calendar)

    while (current.end <= end) {
        yield(current)
        current = current.nextHour()
    }
}

@JvmOverloads
fun minuteRanges(
    start: ZonedDateTime,
    minuteCount: Int = 1,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<MinuteRange> {
    minuteCount.assertPositiveNumber("minuteCount")
    return minuteRanges(start, start + minuteCount.minutes(), calendar)
}

fun minuteRanges(
    start: ZonedDateTime,
    end: ZonedDateTime,
    calendar: ITimeCalendar = TimeCalendar.Default,
): Sequence<MinuteRange> = sequence {
    var current = MinuteRange(start, calendar)

    while (current.end <= end) {
        yield(current)
        current = current.nextMinute()
    }
}
