package io.bluetape4k.utils.times.period

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.Local

/**
 * Period 관련 작업 시 사용할 정보를 담은 context
 *
 * @constructor Create empty Period context
 */
open class PeriodContext: AbstractValueObject() {

    companion object: KLogging() {
        @JvmField
        val TIME_CALENDAR_KEY: String = PeriodContext::class.java.name + ".current"
    }

    object Current: KLogging() {

        var calendar: ITimeCalendar
            get() = Local.getOrPut(TIME_CALENDAR_KEY) { TimeCalendar.Default }!!
            set(value) {
                Local[TIME_CALENDAR_KEY] = value
            }
    }

    override fun equalProperties(other: Any): Boolean {
        return other is PeriodContext
    }
}
