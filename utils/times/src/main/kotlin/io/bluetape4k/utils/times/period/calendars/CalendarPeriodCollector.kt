package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.period.ITimeCalendar
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.SeekDirection
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.period.TimePeriodCollection
import io.bluetape4k.utils.times.period.ranges.CalendarTimeRange
import io.bluetape4k.utils.times.period.ranges.DayRange
import io.bluetape4k.utils.times.period.ranges.DayRangeCollection
import io.bluetape4k.utils.times.period.ranges.DayRangeInMonth
import io.bluetape4k.utils.times.period.ranges.HourRange
import io.bluetape4k.utils.times.period.ranges.MonthRange
import io.bluetape4k.utils.times.period.ranges.MonthRangeCollection
import io.bluetape4k.utils.times.period.ranges.YearRange
import io.bluetape4k.utils.times.period.ranges.YearRangeCollection
import io.bluetape4k.utils.times.zonedDateTimeOf

class CalendarPeriodCollector private constructor(
    filter: CalendarPeriodCollectorFilter,
    limits: ITimePeriod,
    seekDir: SeekDirection,
    calendar: ITimeCalendar,
) : CalendarVisitor<CalendarPeriodCollectorFilter,
    CalendarPeriodCollectorContext>(filter, limits, seekDir, calendar) {

    companion object : KLogging() {
        @JvmStatic
        operator fun invoke(
            filter: CalendarPeriodCollectorFilter,
            limits: ITimePeriod,
            seekDir: SeekDirection = SeekDirection.FORWARD,
            calendar: ITimeCalendar = TimeCalendar.Default,
        ): CalendarPeriodCollector {
            return CalendarPeriodCollector(filter, limits, seekDir, calendar)
        }
    }

    val periods: ITimePeriodCollection = TimePeriodCollection()

    suspend fun collectYears(): Unit = collectByScope(CollectKind.Year)
    suspend fun collectMonths() = collectByScope(CollectKind.Month)
    suspend fun collectDays() = collectByScope(CollectKind.Day)
    suspend fun collectHours() = collectByScope(CollectKind.Hour)
    suspend fun collectMinutes() = collectByScope(CollectKind.Minute)

    private suspend fun collectByScope(scope: CollectKind) {
        val context = CalendarPeriodCollectorContext(scope)
        startPeriodVisit(context)
    }

    override fun enterYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext): Boolean =
        context.scope.value > CollectKind.Year.value

    override fun enterMonths(year: YearRange, context: CalendarPeriodCollectorContext): Boolean =
        context.scope.value > CollectKind.Month.value

    override fun enterDays(month: MonthRange, context: CalendarPeriodCollectorContext): Boolean =
        context.scope.value > CollectKind.Day.value

    override fun enterHours(day: DayRange, context: CalendarPeriodCollectorContext): Boolean =
        context.scope.value > CollectKind.Hour.value

    override fun enterMinutes(hour: HourRange, context: CalendarPeriodCollectorContext): Boolean =
        context.scope.value > CollectKind.Minute.value

    override fun onVisitYears(years: YearRangeCollection, context: CalendarPeriodCollectorContext): Boolean {
        log.trace { "visit years... years=$years, context=$context" }

        if (context.scope != CollectKind.Year) {
            return true
        }

        years.yearSequence()
            .filter { isLimits(it) }
            .filter { isMatchingYear(it, context) }
            .forEach { periods.add(it) }

        return false
    }

    override fun onVisitYear(year: YearRange, context: CalendarPeriodCollectorContext): Boolean {
        log.trace { "visit year... year=$year, context=$context" }

        if (context.scope != CollectKind.Month) {
            return true
        }

        val monthFilter = { mr: MonthRange ->
            isLimits(mr) && isMatchingMonth(mr, context)
        }

        if (filter.collectingMonths.isEmpty()) {
            year.monthSequence().filter(monthFilter).forEach { periods.add(it) }
        } else {
            filter.collectingMonths.forEach { m ->
                val startTime = zonedDateTimeOf(year.year, m.startMonthOfYear)

                if (m.isSingleMonth) {
                    val mr = MonthRange(startTime, year.calendar)
                    if (monthFilter(mr))
                        periods += mr
                } else {
                    val mrc = MonthRangeCollection(
                        startTime,
                        m.endMonthOfYear - m.startMonthOfYear,
                        year.calendar
                    )
                    if (isLimits(mrc)) {
                        val months = mrc.months()
                        val isMatch = months.all { isMatchingMonth(it, context) }
                        if (isMatch) {
                            periods.addAll(months)
                        }
                    }
                }
            }
        }

        return false
    }

    override fun onVisitMonth(month: MonthRange, context: CalendarPeriodCollectorContext): Boolean {
        log.trace { "visit month... month=$month, context=$context" }

        if (context.scope != CollectKind.Day) {
            return true
        }

        val dayFilter = { dr: DayRange ->
            isLimits(dr) && isMatchingDay(dr, context)
        }

        if (filter.collectingDays.isEmpty()) {
            month.daySequence().filter(dayFilter).forEach { periods.add(it) }
        } else {
            filter.collectingDays.forEach { day: DayRangeInMonth ->
                val startTime = zonedDateTimeOf(month.year, month.monthOfYear, day.startDayOfMonth)

                if (day.isSingleDay) {
                    val dayRange = DayRange(startTime, month.calendar)
                    if (dayFilter(dayRange))
                        periods += dayRange
                } else {
                    val drc = DayRangeCollection(
                        startTime,
                        day.endDayOfMonth - day.startDayOfMonth,
                        month.calendar
                    )
                    if (isLimits(drc)) {
                        val days = drc.days()
                        val isMatch = days.all { isMatchingDay(it, context) }
                        if (isMatch) {
                            periods.addAll(days)
                        }
                    }
                }
            }
        }

        return false
    }

    override fun onVisitDay(day: DayRange, context: CalendarPeriodCollectorContext): Boolean {
        log.trace { "visit day... day=$day, context=$context" }

        if (filter.collectingHours.isEmpty()) {
            day.hourSequence()
                .filter { isLimits(it) && isMatchingHour(it, context) }
                .forEach { periods.add(it) }
        } else if (isMatchingDay(day, context)) {
            filter.collectingHours.forEach { h ->
                val start = zonedDateTimeOf(day.start.toLocalDate(), h.start)
                val end = zonedDateTimeOf(day.start.toLocalDate(), h.end)
                val hc = CalendarTimeRange(start, end, day.calendar)
                if (isExcludePeriod(hc) && isLimits(hc)) {
                    periods.add(hc)
                }
            }
        }

        return false
    }
}
