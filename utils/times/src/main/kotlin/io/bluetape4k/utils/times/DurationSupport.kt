package io.bluetape4k.utils.times

import io.bluetape4k.utils.times.TimeSpec.DaysPerWeek
import io.bluetape4k.utils.times.TimeSpec.MonthsPerQuarter
import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import java.time.temporal.Temporal


operator fun Duration.unaryMinus(): Duration = this.negated()

val Duration.isPositive: Boolean get() = this > Duration.ZERO

val Duration.isNotNegative: Boolean get() = this >= Duration.ZERO

/**
 * Duration을 millseconds 로 환산
 */
fun Duration.inMillis(): Long = toMillis()

/**
 * Duration을 nano seconds로 환산
 */
fun Duration.inNanos(): Long = toNanos() // seconds * NANO_PER_SECOND + nano

/**
 * [startInclusive] ~ [endExclusive] 의 기간을 [Duration]으로 빌드합니다.
 *
 * @param startInclusive 시작 시각
 * @param endExclusive 끝 시각
 * @return Duration
 */
fun durationOf(startInclusive: Temporal, endExclusive: Temporal): Duration =
    Duration.between(startInclusive, endExclusive)

/**
 * [year]의 1년 단위의 기간
 *
 * ```kotlin
 * val duration = durationOfYear(2020)  // 2020.01.01 ~ 20202.12.31
 * ```
 * @param year Int
 * @return Duration
 */
fun durationOfYear(year: Int): Duration =
    durationOf(zonedDateTimeOf(year), zonedDateTimeOf(year + 1))

/**
 * [year]의 해당 [quarter]의 기간
 *
 * @param year Int
 * @param quarter Quarter
 * @return Duration
 */
fun durationOfQuarter(year: Int, quarter: Quarter): Duration {
    val startInclusive = startOfQuarter(year, quarter)
    val endExclusive = startInclusive.plusMonths(MonthsPerQuarter.toLong())
    return durationOf(startInclusive, endExclusive)
}

/**
 * [year]의 해당 [monthOfYear]의 기간
 * @param year Int
 * @param monthOfYear Int
 * @return Duration
 */
fun durationOfMonth(year: Int, monthOfYear: Int): Duration {
    val startInclusive = startOfMonth(year, monthOfYear)
    val endExclusive = startInclusive.plusMonths(1)
    return durationOf(startInclusive, endExclusive)
}

/**
 * [week] 수에 해당하는 기간
 *
 * ```kotlin
 * val duration = durationOfWeek(3)     // 3주간의 duration == (3 * 7).days()
 * ```
 * @param week Int
 * @return Duration
 */
fun durationOfWeek(week: Int): Duration = if (week == 0) Duration.ZERO else durationOfDay(week * DaysPerWeek)

/**
 * 지정한 시간 단위별 기간을 조합합니다.
 *
 * @param days Int
 * @param hours Int
 * @param minutes Int
 * @param seconds Int
 * @param nanos Int
 * @return Duration
 */
@JvmOverloads
fun durationOfDay(
    days: Int,
    hours: Int = 0,
    minutes: Int = 0,
    seconds: Int = 0,
    nanos: Int = 0,
): Duration {
    var duration = days.days()

    if (hours != 0)
        duration += hours.hours()
    if (minutes != 0)
        duration += minutes.minutes()
    if (seconds != 0)
        duration += seconds.seconds()
    if (nanos != 0)
        duration += nanos.nanos()

    return duration
}

/**
 * 지정한 시간 단위별 기간을 조합합니다.
 *
 * @param hours Int
 * @param minutes Int
 * @param seconds Int
 * @param nanos Int
 * @return Duration
 */
@JvmOverloads
fun durationOfHour(
    hours: Int,
    minutes: Int = 0,
    seconds: Int = 0,
    nanos: Int = 0,
): Duration {
    var duration = hours.hours()

    if (minutes != 0)
        duration += minutes.minutes()
    if (seconds != 0)
        duration += seconds.seconds()
    if (nanos != 0)
        duration += nanos.nanos()

    return duration
}

/**
 * 지정한 시간 단위별 기간을 조합합니다.
 *
 * @param minutes Int
 * @param seconds Int
 * @param nanos Int
 * @return Duration
 */
@JvmOverloads
fun durationOfMinute(
    minutes: Int,
    seconds: Int = 0,
    nanos: Int = 0,
): Duration {
    var duration = minutes.minutes()

    if (seconds != 0)
        duration += seconds.seconds()
    if (nanos != 0)
        duration += nanos.nanos()

    return duration
}

/**
 * 지정한 시간 단위별 기간을 조합합니다.
 *
 * @param seconds Int
 * @param nanos Int
 * @return Duration
 */
@JvmOverloads
fun durationOfSecond(
    seconds: Int,
    nanos: Int = 0,
): Duration {
    var duration = seconds.seconds()

    if (nanos != 0)
        duration += nanos.nanos()

    return duration
}

/**
 * [nanos] 수에 해당하는 Duration을 빌드합니다.
 * @param nanos nano seconds
 * @return Duration
 */
fun durationOfNano(nanos: Long): Duration = Duration.ofNanos(nanos)


fun Int.asNanos(): Duration = Duration.ofNanos(this.toLong())
fun Int.asMillis(): Duration = Duration.ofMillis(this.toLong())

@JvmOverloads
fun Int.asSeconds(nanoAdjustment: Long = 0L): Duration = Duration.ofSeconds(this.toLong(), nanoAdjustment)
fun Int.asMinutes(): Duration = Duration.ofMinutes(this.toLong())
fun Int.asHours(): Duration = Duration.ofHours(this.toLong())
fun Int.asDays(): Duration = Duration.ofDays(this.toLong())

fun Long.asNanos(): Duration = Duration.ofNanos(this)
fun Long.asMillis(): Duration = Duration.ofMillis(this)

@JvmOverloads
fun Long.asSeconds(nanoAdjustment: Long = 0L): Duration = Duration.ofSeconds(this, nanoAdjustment)
fun Long.asMinutes(): Duration = Duration.ofMinutes(this)
fun Long.asHours(): Duration = Duration.ofHours(this)
fun Long.asDays(): Duration = Duration.ofDays(this)


/**
 * [Duration]을 ISO Format의 문자열로 만듭니다
 */
fun Duration.formatISO(): String = DurationFormatUtils.formatDurationISO(inMillis())

/**
 * [Duration]을 시간 포맷 (HH:mm:ss.SSS)의 문자열로 변환합니다.
 *
 * @return
 */
fun Duration.formatHMS(): String = DurationFormatUtils.formatDurationHMS(inMillis())


private val durationIsoFormat: Regex =
    """P(?<year>\d)Y(?<month>\d)M(?<day>\d)DT(?<hour>\d)H(?<minute>\d)M(?<second>\d)\.(?<mills>\d{3})S""".toRegex()

/**
 * ISO Format으로 표현된 Duration 정보를 파싱해서 Duration으로 변경한다.
 *
 * @param isoFormattedString ISO Formatted Duration
 * @return [Duration] instance
 */
fun parseIsoFormattedDuration(isoFormattedString: String): Duration? {
    val matchResult = durationIsoFormat.matchEntire(isoFormattedString)
    return matchResult?.let {
        val (_, _, d, h, min, s, ms) = it.destructured
        Duration.ofDays(d.toLong())
            .plusHours(h.toLong())
            .plusMinutes(min.toLong())
            .plusSeconds(s.toLong())
            .plusMillis(ms.toLong())
    }
}
