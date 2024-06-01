package io.bluetape4k.times

import java.time.Duration
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount


// NOTE: ChronoUnit.DAYS 아래만 가능합니다. (Period 는 정확한 계산할 수 없습니다)
//

val TemporalAmount.nanos: Double
    get() = units.fold(0.0) { acc, it -> acc + Duration.of(get(it), it).toNanos().toDouble() }

// NOTE: ChronoUnit.DAYS 아래만 가능합니다. (Period 는 정확한 계산할 수 없습니다)
//

val TemporalAmount.millis: Long
    get() = units.fold(0L) { acc, unit -> acc + Duration.of(get(unit), unit).toMillis() }

val TemporalAmount.isZero: Boolean
    get() = millis == 0L

val TemporalAmount.isPositive: Boolean
    get() = millis > 0L

val TemporalAmount.isNegative: Boolean
    get() = millis < 0L


fun Int.temporalAmount(chronoUnit: ChronoUnit): TemporalAmount = toLong().temporalAmount(chronoUnit)

fun Long.temporalAmount(chronoUnit: ChronoUnit): TemporalAmount = when (chronoUnit) {
    ChronoUnit.YEARS   -> Period.ofYears(this.toInt())
    ChronoUnit.MONTHS  -> Period.ofMonths(this.toInt())
    ChronoUnit.WEEKS   -> Period.ofWeeks(this.toInt())
    ChronoUnit.DAYS    -> Duration.ofDays(this)
    ChronoUnit.HOURS   -> Duration.ofHours(this)
    ChronoUnit.MINUTES -> Duration.ofMinutes(this)
    ChronoUnit.SECONDS -> Duration.ofSeconds(this)
    ChronoUnit.MILLIS  -> Duration.ofMillis(this)
    ChronoUnit.MICROS  -> Duration.ofNanos(this * 1000L)
    ChronoUnit.NANOS   -> Duration.ofNanos(this)
    else               -> throw IllegalArgumentException("Not supported ChronoUnit. chronounit=$chronoUnit")
}
