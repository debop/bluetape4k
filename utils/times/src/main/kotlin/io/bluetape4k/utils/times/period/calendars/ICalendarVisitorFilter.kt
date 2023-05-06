package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.core.ValueObject
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import java.time.DayOfWeek

/**
 * Calendar 탐색 시의 필터링을 할 조건을 표현하는 인터페이스입니다.
 */
interface ICalendarVisitorFilter: ValueObject {

    val excludePeriods: ITimePeriodCollection

    val years: IntArrayList

    val monthOfYears: IntArrayList

    val dayOfMonths: IntArrayList

    val dayOfWeeks: MutableSet<DayOfWeek>

    val hourOfDays: IntArrayList

    val minuteOfHours: IntArrayList

    fun addWorkingWeekdays()

    fun addWorkingWeekends()

    fun addDayOfWeeks(dayOfWeeks: Set<DayOfWeek>)

    fun clear()

}
