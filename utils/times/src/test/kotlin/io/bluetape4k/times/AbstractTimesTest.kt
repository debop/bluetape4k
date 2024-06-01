package io.bluetape4k.times

import io.bluetape4k.logging.KLogging
import java.time.temporal.ChronoUnit

abstract class AbstractTimesTest {

    companion object: KLogging()

    val chronoUnits = listOf(
        ChronoUnit.YEARS,
        ChronoUnit.MONTHS,
        ChronoUnit.WEEKS,
        ChronoUnit.DAYS,
        ChronoUnit.HOURS,
        ChronoUnit.MINUTES,
        ChronoUnit.SECONDS,
        ChronoUnit.MILLIS
    )
}
