package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.support.hashOf
import java.time.DayOfWeek

/**
 * 특정 요일의 하루 동안의 시간 간격
 */
open class DayOfWeekHourRange(
    val dayOfWeek: DayOfWeek,
    startHourOfDay: Int = 0,
    endHourOfDay: Int = 23,
): HourRangeInDay(startHourOfDay, endHourOfDay) {

    init {
        check(startHourOfDay in 0..23) { "startHourOfDay[$startHourOfDay must be 0..23" }
        check(endHourOfDay in 0..23) { "endHourOfDay[$endHourOfDay must be 0..23" }
        check(startHourOfDay <= endHourOfDay) {
            "startHourOfDay[$startHourOfDay must be less than or equals endDayOfHour[$endHourOfDay]"
        }
    }

    override fun hashCode(): Int = hashOf(super.hashCode(), dayOfWeek)

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun buildStringHelper(): ToStringBuilder =
        super.buildStringHelper()
            .add("dayOfWeek", dayOfWeek)
}
