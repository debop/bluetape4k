package io.bluetape4k.utils.times.period

import io.bluetape4k.core.ValueObject
import java.time.temporal.TemporalAccessor
import java.time.temporal.WeekFields

/**
 * Weekyear and Week
 */
data class WeekyearWeek(
    val weekyear: Int,
    val weekOfWeekyear: Int,
) : ValueObject {

    companion object {
        operator fun invoke(moment: TemporalAccessor): WeekyearWeek {
            val weekyear = moment[WeekFields.ISO.weekBasedYear()]
            val weekOfWeekyear = moment[WeekFields.ISO.weekOfWeekBasedYear()]
            return WeekyearWeek(weekyear, weekOfWeekyear)
        }
    }
}
