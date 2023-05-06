package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.MaxPeriodTime
import io.bluetape4k.utils.times.MinPeriodTime
import io.bluetape4k.utils.times.Weekdays
import io.bluetape4k.utils.times.Weekends
import io.bluetape4k.utils.times.isNotNegative
import io.bluetape4k.utils.times.nanos
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.SeekBoundaryMode
import io.bluetape4k.utils.times.period.SeekDirection
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.TimePeriodCollection
import io.bluetape4k.utils.times.period.TimeRange
import io.bluetape4k.utils.times.period.ranges.DayOfWeekHourRange
import io.bluetape4k.utils.times.period.ranges.HourRangeInDay
import io.bluetape4k.utils.times.period.ranges.WeekRange
import io.bluetape4k.utils.times.period.timelines.TimeGapCalculator
import java.time.DayOfWeek
import java.time.Duration
import java.time.ZonedDateTime

open class CalendarDateAdd private constructor(): DateAdd() {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(): CalendarDateAdd = CalendarDateAdd()
    }

    val calendar: TimeCalendar = TimeCalendar.EmptyOffset
    val weekDays: MutableList<DayOfWeek> = fastListOf()
    val workingHours: MutableList<HourRangeInDay> = fastListOf()
    val workingDayOfWeekHours: MutableList<DayOfWeekHourRange> = fastListOf()

    override val includePeriods: TimePeriodCollection
        get() = throw UnsupportedOperationException("Does not support IncludePeriods")

    fun addWorkingWeekdays() = addWeekdays(*Weekdays)

    fun addWeekendWeekdays() = addWeekdays(*Weekends)

    fun addWeekdays(vararg dayOfWeeks: DayOfWeek) {
        weekDays.addAll(dayOfWeeks.asList())
    }

    override fun add(start: ZonedDateTime, offset: Duration, seekBoundary: SeekBoundaryMode): ZonedDateTime? {
        log.trace { "add... start=$start, offset=$offset, seekBoundary=$seekBoundary" }

        val allEmpty = weekDays.isEmpty() && _excludePeriods.isEmpty() && workingHours.isEmpty()
        if (allEmpty) {
            return start + offset
        }

        val (end, remaining) = when {
            offset.isNegative -> calculateEnd(start, offset.negated(), SeekDirection.BACKWARD, seekBoundary)
            else              -> calculateEnd(start, offset, SeekDirection.FORWARD, seekBoundary)
        }

        log.trace { "added... endInclusive=$end, remaining=$remaining" }
        return end
    }

    override fun subtract(start: ZonedDateTime, offset: Duration, seekBoundary: SeekBoundaryMode): ZonedDateTime? {
        log.trace { "subtract... start=$start, offset=$offset, seekBoundary=$seekBoundary" }

        val allEmpty = weekDays.isEmpty() && _excludePeriods.isEmpty() && workingHours.isEmpty()
        if (allEmpty) {
            return start - offset
        }

        val (endInclusive, remaining) = when {
            offset.isNegative -> calculateEnd(start, offset.negated(), SeekDirection.FORWARD, seekBoundary)
            else              -> calculateEnd(start, offset, SeekDirection.BACKWARD, seekBoundary)
        }

        log.trace { "subtract... endInclusive=$endInclusive, remaining=$remaining" }
        return endInclusive
    }

    override fun calculateEnd(
        start: ZonedDateTime,
        offset: Duration?,
        seekDir: SeekDirection,
        seekBoundary: SeekBoundaryMode,
    ): Pair<ZonedDateTime?, Duration?> {
        log.trace {
            "기준 시각으로부터 offset 만큼 떨어진 시각을 구합니다..." +
                "start=$start, offset=$offset, seekDir=$seekDir, seekBoundary=$seekBoundary"
        }

        check(offset?.isNotNegative ?: false) { "offset 값은 0 이사이어야 합니다. offset=$offset" }

        var moment = start
        var end: ZonedDateTime? = null
        var remaining: Duration? = offset

        var week: WeekRange? = WeekRange(moment, calendar)

        while (week != null) {
            _includePeriods.clear()
            _includePeriods.addAll(getAvailableWeekPeriods(week))

            log.trace { "가능한 기간=$_includePeriods" }

            val result = super.calculateEnd(moment, remaining!!, seekDir, seekBoundary)
            end = result.first
            remaining = result.second

            log.trace { "완료기간을 구했습니다. end=$end, remaining=$remaining" }

            if (end != null || remaining == null) {
                log.trace { "결과. endInclusive=$end, remaining=$remaining" }
                return Pair(end, remaining)
            }

            when (seekDir) {
                SeekDirection.FORWARD -> {
                    week = findNextWeek(week)
                    week?.let { moment = it.start }
                }

                else -> {
                    week = findPrevWeek(week)
                    week?.let { moment = it.end }
                }
            }
        }

        log.trace { "Calculate done. end=$end, remaining=$remaining" }
        return Pair(end, remaining)
    }

    private fun findNextWeek(current: WeekRange): WeekRange? {
        log.trace { "current week=$current 의 이후 week 기간을 구합니다..." }

        val nextWeek = when {
            _excludePeriods.isEmpty() -> {
                current.nextWeek()
            }

            else -> {
                val limits = TimeRange(start = current.end + 1.nanos(), end = MaxPeriodTime)
                val gapCalculator = TimeGapCalculator<TimeRange>(calendar)
                val remainingPeriods = gapCalculator.gaps(_excludePeriods, limits)

                if (remainingPeriods.isNotEmpty()) {
                    WeekRange(remainingPeriods.first().start, calendar)
                } else {
                    null
                }
            }
        }
        log.trace { "current week=$current, next week=$nextWeek" }
        return nextWeek
    }

    private fun findPrevWeek(current: WeekRange): WeekRange? {
        log.trace { "current week=$current 의 이전 week 기간을 구합니다..." }

        val prevWeek = when {
            _excludePeriods.isEmpty() -> {
                current.prevWeek()
            }

            else -> {
                val limits = TimeRange(start = MinPeriodTime, end = current.start - 1.nanos())
                val gapCalculator = TimeGapCalculator<TimeRange>(calendar)
                val remainingPeriods = gapCalculator.gaps(_excludePeriods, limits)

                if (remainingPeriods.isNotEmpty()) {
                    WeekRange(remainingPeriods.last().end, calendar)
                } else {
                    null
                }
            }
        }
        log.trace { "current week=$current, prev week=$prevWeek" }
        return prevWeek
    }

    private fun getAvailableWeekPeriods(limits: ITimePeriod): List<ITimePeriod> {
        log.trace { "가능한 주간 기간을 추출합니다. limits=$limits" }

        val workingDayIsEmpty = weekDays.isEmpty() && workingHours.isEmpty() && workingDayOfWeekHours.isEmpty()
        if (workingDayIsEmpty) {
            return TimePeriodCollection(limits)
        }

        val filter = CalendarPeriodCollectorFilter()

        weekDays.forEach { filter.addDayOfWeeks(it) }

        filter.collectingHours.addAll(workingHours)
        filter.collectingDayOfWeekHours.addAll(workingDayOfWeekHours)

        val weekCollector = CalendarPeriodCollector(filter, limits, SeekDirection.FORWARD, calendar)
        weekCollector.collectHours()

        log.trace { "available week periods=${weekCollector.periods}" }
        return weekCollector.periods
    }
}
