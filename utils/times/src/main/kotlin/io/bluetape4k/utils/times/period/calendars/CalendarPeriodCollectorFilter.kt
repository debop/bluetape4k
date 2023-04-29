package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.utils.times.period.ranges.DayOfWeekHourRange
import io.bluetape4k.utils.times.period.ranges.DayRangeInMonth
import io.bluetape4k.utils.times.period.ranges.HourRangeInDay
import io.bluetape4k.utils.times.period.ranges.MonthRangeInYear

open class CalendarPeriodCollectorFilter: CalendarVisitorFilter(), ICalendarPeriodCollectorFilter {

    override val collectingMonths: MutableList<MonthRangeInYear> = fastListOf()

    override val collectingDays: MutableList<DayRangeInMonth> = fastListOf()

    override val collectingHours: MutableList<HourRangeInDay> = fastListOf()

    override val collectingDayOfWeekHours: MutableList<DayOfWeekHourRange> = fastListOf()

    override fun clear() {
        super.clear()

        collectingMonths.clear()
        collectingDays.clear()
        collectingHours.clear()
        collectingDayOfWeekHours.clear()
    }
}
