package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.utils.times.TimeSpec.Weekdays
import io.bluetape4k.utils.times.TimeSpec.Weekends
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.TimePeriodCollection
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import java.io.Serializable
import java.time.DayOfWeek
import java.time.Month


/**
 * Calendar 탐색 시의 필터링을 할 조건을 가지는 클래스입니다.
 */
open class CalendarVisitorFilter : AbstractValueObject(), ICalendarVisitorFilter, Serializable {

    override val excludePeriods: ITimePeriodCollection = TimePeriodCollection()

    override val years: IntArrayList = IntArrayList()

    override val monthOfYears: IntArrayList = IntArrayList()

    override val dayOfMonths: IntArrayList = IntArrayList()

    override val dayOfWeeks: MutableSet<DayOfWeek> = unifiedSetOf()

    override val hourOfDays: IntArrayList = IntArrayList()

    override val minuteOfHours: IntArrayList = IntArrayList()

    fun addYears(vararg years: Int) {
        this.years.addAll(*years)
    }

    fun addMonthOfYears(vararg months: Month) {
        months.forEach { monthOfYears.add(it.value) }
    }

    fun addMonthOfYears(vararg monthOfYears: Int) {
        this.monthOfYears.addAll(*monthOfYears)
    }

    fun addDayOfMonths(vararg days: Int) {
        this.dayOfMonths.addAll(*days)
    }

    fun addDayOfWeeks(vararg dows: DayOfWeek) {
        this.dayOfWeeks.addAll(dows)
    }

    fun addHourOfDays(vararg hourOfDays: Int) {
        this.hourOfDays.addAll(*hourOfDays)
    }

    fun addMinuteOfHours(vararg minuteOfHours: Int) {
        this.minuteOfHours.addAll(*minuteOfHours)
    }

    override fun addWorkingWeekdays() {
        addDayOfWeeks(Weekdays.toSet())
    }

    override fun addWorkingWeekends() {
        addDayOfWeeks(Weekends.toSet())
    }

    override fun addDayOfWeeks(dayOfWeeks: Set<DayOfWeek>) {
        this.dayOfWeeks.addAll(dayOfWeeks)
    }

    override fun clear() {
        years.clear()
        monthOfYears.clear()
        dayOfMonths.clear()
        dayOfWeeks.clear()
        hourOfDays.clear()
        minuteOfHours.clear()
    }

    override fun equalProperties(other: Any): Boolean {
        return other is CalendarVisitorFilter &&
               years == other.years &&
               monthOfYears == other.monthOfYears &&
               dayOfMonths == other.dayOfMonths &&
               minuteOfHours == other.minuteOfHours &&
               dayOfWeeks == other.dayOfWeeks &&
               excludePeriods == other.excludePeriods
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("years", years)
            .add("monthOfYears", monthOfYears)
            .add("dayOfMonths", dayOfMonths)
            .add("hourOfDays", hourOfDays)
            .add("minuteOfHours", minuteOfHours)
            .add("dayOfWeeks", dayOfWeeks)
            .add("excludePeriods", excludePeriods)
    }
}
