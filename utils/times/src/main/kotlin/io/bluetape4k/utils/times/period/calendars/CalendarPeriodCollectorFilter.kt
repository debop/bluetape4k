package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.utils.times.period.ranges.DayOfWeekHourRange
import io.bluetape4k.utils.times.period.ranges.DayRangeInMonth
import io.bluetape4k.utils.times.period.ranges.HourRangeInDay
import io.bluetape4k.utils.times.period.ranges.MonthRangeInYear

open class CalendarPeriodCollectorFilter: CalendarVisitorFilter(), ICalendarPeriodCollectorFilter {

    override val collectingMonths: MutableList<MonthRangeInYear> = mutableListOf()

    override val collectingDays: MutableList<DayRangeInMonth> = mutableListOf()

    override val collectingHours: MutableList<HourRangeInDay> = mutableListOf()

    override val collectingDayOfWeekHours: MutableList<DayOfWeekHourRange> = mutableListOf()

    override fun clear() {
        super.clear()

        collectingMonths.clear()
        collectingDays.clear()
        collectingHours.clear()
        collectingDayOfWeekHours.clear()
    }
}
