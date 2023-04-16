package io.bluetape4k.utils.times.period

import io.bluetape4k.core.assertZeroOrPositiveNumber
import io.bluetape4k.utils.times.DaysPerWeek
import io.bluetape4k.utils.times.MonthsPerQuarter
import io.bluetape4k.utils.times.isPositive
import io.bluetape4k.utils.times.max
import io.bluetape4k.utils.times.min
import io.bluetape4k.utils.times.monthPeriod
import io.bluetape4k.utils.times.startOfDay
import io.bluetape4k.utils.times.startOfHour
import io.bluetape4k.utils.times.startOfMinute
import io.bluetape4k.utils.times.startOfMonth
import io.bluetape4k.utils.times.startOfQuarter
import io.bluetape4k.utils.times.startOfSecond
import io.bluetape4k.utils.times.startOfWeek
import io.bluetape4k.utils.times.startOfYear
import io.bluetape4k.utils.times.yearPeriod
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.Temporal


fun <T> adjustPeriod(left: T?, right: T?): Pair<T?, T?> where T: Temporal, T: Comparable<T> =
    Pair(left min right, left max right)


fun adjustPeriod(start: ZonedDateTime, duration: Duration): Pair<ZonedDateTime, Duration> = when {
    duration.isPositive -> Pair(start, duration)
    else -> Pair(start - duration, duration.negated())
}

fun assertValidPeriod(start: ZonedDateTime?, end: ZonedDateTime?) {
    if (start != null && end != null) {
        assert(start <= end) { "시작시각이 완료시각 이전이어야 합니다. start=$start, end=$end" }
    }
}

fun <T: ITimePeriod> allItemsAreEquals(left: Iterable<T>, right: Iterable<T>): Boolean {
    val leftIter = left.iterator()
    val rightIter = right.iterator()

    while (leftIter.hasNext() && rightIter.hasNext()) {
        if (!leftIter.next().isSamePeriod(rightIter.next())) {
            return false
        }
    }
    return !leftIter.hasNext() && !rightIter.hasNext()
}

fun ZonedDateTime.toTimeBlock(duration: Duration): ITimeBlock = TimeBlock(this, duration)

fun ZonedDateTime.toTimeBlock(end: ZonedDateTime): ITimeBlock = TimeBlock(this, end)

fun ZonedDateTime.toTimeRange(duration: Duration): ITimeRange = TimeRange(this, duration)

fun ZonedDateTime.toTimeRange(end: ZonedDateTime): ITimeRange = TimeRange(this, end)


fun yearOf(year: Int, monthOfYear: Int, calendar: ITimeCalendar = TimeCalendar.Default): Int = when {
    monthOfYear in 1..12 -> year
    monthOfYear < calendar.baseMonth -> year - 1
    monthOfYear > 12 -> year + 1
    else -> throw IllegalArgumentException("Invalid monthOfYear[$monthOfYear]")
}

fun ZonedDateTime.yearOf(): Int = yearOf(year, monthValue)

fun relativeYearPeriodOf(year: Int, yearCount: Int = 1): ITimeRange {
    yearCount.assertZeroOrPositiveNumber("yearCount")

    val start = startOfYear(year)
    return TimeRange(start, start + yearCount.yearPeriod())
}

fun ZonedDateTime.relativeYearPeriod(yearCount: Int): ITimeRange {
    yearCount.assertZeroOrPositiveNumber("yearCount")

    val start = this.startOfYear()
    return TimeRange(start, start + yearCount.yearPeriod())
}

fun ZonedDateTime.relativeQuarterPeriod(quarterCount: Int = 1): ITimeRange {
    quarterCount.assertZeroOrPositiveNumber("quarterCount")

    val start = this.startOfQuarter()
    val months = quarterCount * MonthsPerQuarter
    return TimeRange(start, start.plusMonths(months.toLong()))
}

fun ZonedDateTime.relativeMonthPeriod(monthCount: Int): ITimeRange {
    monthCount.assertZeroOrPositiveNumber("monthCount")

    val start = this.startOfMonth()
    return TimeRange(start, start + monthCount.monthPeriod())
}

fun ZonedDateTime.relativeWeekPeriod(weekCount: Int = 1): ITimeRange {
    weekCount.assertZeroOrPositiveNumber("weekCount")

    val start = this.startOfWeek()
    return TimeRange(start, start.plusDays(weekCount.toLong() * DaysPerWeek))
}

fun ZonedDateTime.relativeDayPeriod(dayCount: Int = 1): TimeRange {
    dayCount.assertZeroOrPositiveNumber("dayCount")

    val start = this.startOfDay()
    return TimeRange(start, start.plusDays(dayCount.toLong()))
}

fun ZonedDateTime.relativeHourPeriod(hourCount: Int = 1): TimeRange {
    hourCount.assertZeroOrPositiveNumber("hourCount")

    val start = this.startOfHour()
    return TimeRange(start, start.plusHours(hourCount.toLong()))
}

fun ZonedDateTime.relativeMinutePeriod(minuteCount: Int = 1): TimeRange {
    minuteCount.assertZeroOrPositiveNumber("minuteCount")

    val start = this.startOfMinute()
    return TimeRange(start, start.plusMinutes(minuteCount.toLong()))
}

fun ZonedDateTime.relativeSecondPeriod(secondCount: Int = 1): TimeRange {
    secondCount.assertZeroOrPositiveNumber("secondCount")

    val start = this.startOfSecond()
    return TimeRange(start, start.plusSeconds(secondCount.toLong()))
}


@Suppress("ConvertTwoComparisonsToRangeCheck")
infix fun ITimePeriod.hasInsideWith(moment: ZonedDateTime): Boolean =
    start <= moment && moment <= end

infix fun ITimePeriod.hasInsideWith(that: ITimePeriod): Boolean =
    this.hasInsideWith(that.start) && this.hasInsideWith(that.end)

infix fun ITimePeriod.hasPureInsideWith(moment: ZonedDateTime): Boolean =
    start < moment && moment < end

infix fun ITimePeriod.hasPureInsideWith(that: ITimePeriod): Boolean =
    hasPureInsideWith(that.start) && hasPureInsideWith(that.end)

infix fun ITimePeriod.relationWith(that: ITimePeriod): PeriodRelation = when {
    this.start > that.end -> PeriodRelation.After
    this.end < that.start -> PeriodRelation.Before
    this.isSamePeriod(that) -> PeriodRelation.ExactMatch
    this.start == that.end -> PeriodRelation.StartTouching
    this.end == that.start -> PeriodRelation.EndTouching

    this.hasInsideWith(that) -> when {
        this.start == that.start -> PeriodRelation.EnclosingStartTouching
        this.end == that.end -> PeriodRelation.EnclosingEndTouching
        else -> PeriodRelation.Enclosing
    }

    else -> {
        val isInsideStart = that.hasInsideWith(this.start)
        val isInsideEnd = that.hasInsideWith(this.end)
        when {
            isInsideStart && isInsideEnd -> when {
                this.start == that.start -> PeriodRelation.InsideStartTouching
                this.end == that.end -> PeriodRelation.InsideEndTouching
                else -> PeriodRelation.Inside
            }

            isInsideStart -> PeriodRelation.StartInside
            isInsideEnd -> PeriodRelation.EndInside
            else -> PeriodRelation.NoRelation
        }
    }
}


infix fun ITimePeriod.intersectWith(that: ITimePeriod): Boolean =
    hasInsideWith(that.start) || hasInsideWith(that.end) || that.hasPureInsideWith(this)

infix fun ITimePeriod.overlapWith(that: ITimePeriod): Boolean =
    this.relationWith(that) !in PeriodRelation.NotOverlappedRelations

infix fun ITimePeriod.intersectBlock(that: ITimePeriod): ITimeBlock? {
    var intersection: ITimeBlock? = null

    if (this.intersectWith(that)) {
        val start = this.start max that.start
        val end = this.end min that.end
        intersection = TimeBlock(start, end, this.readonly)
    }

    return intersection
}

infix fun ITimePeriod.intersectRange(that: ITimePeriod): TimeRange? {
    var intersection: TimeRange? = null

    if (this.intersectWith(that)) {
        val start = this.start max that.start
        val end = this.end min that.end
        intersection = TimeRange(start, end, this.readonly)
    }

    return intersection
}

infix fun ITimePeriod.unionBlock(that: ITimePeriod): ITimeBlock {
    val start = this.start min that.start
    val end = this.end max that.end

    return TimeBlock(start, end, this.readonly)
}

infix fun ITimePeriod.unionRange(that: ITimePeriod): TimeRange {
    val start = this.start min that.start
    val end = this.end max that.end

    return TimeRange(start!!, end!!, this.readonly)
}
