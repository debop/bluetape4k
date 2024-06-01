package io.bluetape4k.times.period.calendars.seekers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.times.period.calendars.ICalendarVisitorContext
import io.bluetape4k.times.period.ranges.DayRange
import kotlin.math.absoluteValue

open class DaySeekerContext private constructor(
    val startDay: DayRange,
    val dayCount: Int = 0,
): ICalendarVisitorContext {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(startDay: DayRange, dayCount: Int = 0): DaySeekerContext {
            return DaySeekerContext(startDay, dayCount.absoluteValue)
        }
    }

    var remainingDays: Int = dayCount
    var foundDay: DayRange? = null

    val isFinished: Boolean get() = remainingDays == 0

    val notFinished: Boolean get() = remainingDays != 0

    fun processDay(day: DayRange) {
        if (!isFinished) {
            --remainingDays

            if (isFinished) {
                foundDay = day
            }
        }
    }
}
