package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.core.requireInRange

enum class CollectKind {

    Year, Month, Day, Hour, Minute;

    val value: Int = ordinal

    companion object {

        val VALS = values()

        @JvmStatic
        fun of(value: Int): CollectKind {
            value.requireInRange(0, 4, "value")
            return VALS[value]
        }
    }
}
