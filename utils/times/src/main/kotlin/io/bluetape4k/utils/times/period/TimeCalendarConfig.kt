package io.bluetape4k.utils.times.period

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.times.DefaultEndOffset
import io.bluetape4k.utils.times.DefaultStartOffset
import io.bluetape4k.utils.times.EmptyDuration
import io.bluetape4k.utils.times.FirstDayOfWeek
import java.io.Serializable
import java.time.DayOfWeek
import java.time.Duration
import java.util.Locale

/**
 * TimeCalendarConfig
 */
data class TimeCalendarConfig(
    val locale: Locale = Locale.getDefault(),
    val startOffset: Duration = DefaultStartOffset,
    val endOffset: Duration = DefaultEndOffset,
    val firstDayOfWeek: DayOfWeek = FirstDayOfWeek,
): Serializable {

    companion object: KLogging() {
        @JvmStatic
        val Default: TimeCalendarConfig = TimeCalendarConfig()

        @JvmStatic
        val EmptyOffset: TimeCalendarConfig =
            TimeCalendarConfig(startOffset = EmptyDuration, endOffset = EmptyDuration)
    }
}