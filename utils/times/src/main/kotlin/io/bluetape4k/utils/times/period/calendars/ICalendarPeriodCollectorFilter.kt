package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.utils.times.period.ranges.DayOfWeekHourRange
import io.bluetape4k.utils.times.period.ranges.DayRangeInMonth
import io.bluetape4k.utils.times.period.ranges.HourRangeInDay
import io.bluetape4k.utils.times.period.ranges.MonthRangeInYear

interface ICalendarPeriodCollectorFilter: ICalendarVisitorFilter {

    val collectingMonths: MutableList<MonthRangeInYear>

    val collectingDays: MutableList<DayRangeInMonth>

    val collectingHours: MutableList<HourRangeInDay>

    val collectingDayOfWeekHours: MutableList<DayOfWeekHourRange>
}
