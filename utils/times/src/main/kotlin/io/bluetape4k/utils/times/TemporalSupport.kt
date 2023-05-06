@file:Suppress("UNCHECKED_CAST")

package io.bluetape4k.utils.times

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit


//
// NOTE: [Temporal]에 이미 plus, minus 함수가 있어서 재사용을 못한다
//

fun <T: Temporal> T.add(period: Period): T = this.plus(period) as T
fun <T: Temporal> T.add(duration: Duration): T = this.plus(duration) as T
fun <T: Temporal> T.add(amount: TemporalAmount): T = this.plus(amount) as T

fun <T: Temporal> T.subtract(period: Period): T = this.minus(period) as T
fun <T: Temporal> T.subtract(duration: Duration): T = this.minus(duration) as T
fun <T: Temporal> T.subtract(amount: TemporalAmount): T = this.minus(amount) as T

// Temporal Adjusters

val <T: Temporal> T.firstOfMonth: T get() = with(TemporalAdjusters.firstDayOfMonth()) as T
val <T: Temporal> T.lastOfMonth: T get() = with(TemporalAdjusters.lastDayOfMonth()) as T
val <T: Temporal> T.firstOfNextMonth: T get() = with(TemporalAdjusters.firstDayOfNextMonth()) as T

// FIXME: startOfDay() 가 사용하는 truncatedTo 함수가 DayOfYear 를 지원하지 않습니다.
@Deprecated("use startOfYear()", replaceWith = ReplaceWith("startOfYear()"))
val <T: Temporal> T.firstOfYear: T get() = with(TemporalAdjusters.firstDayOfYear()) as T

// FIXME: endOfDay() 가 사용하는 truncatedTo 함수가 DayOfYear 를 지원하지 않습니다.
@Deprecated("use endOfYear()", replaceWith = ReplaceWith("endOfYear()"))
val <T: Temporal> T.lastOfYear: T get() = with(TemporalAdjusters.lastDayOfYear()) as T

// FIXME: startOfNextDay() 가 사용하는 truncatedTo 함수가 DayOfYear 를 지원하지 않습니다.
@Deprecated("use startOfNextYear()", replaceWith = ReplaceWith("startOfNextYear()"))
val <T: Temporal> T.firstOfNextYear: T get() = with(TemporalAdjusters.firstDayOfNextYear()) as T

fun <T: Temporal> T.dayOfWeekInMonth(ordinal: Int, dayOfWeek: DayOfWeek): T =
    with(TemporalAdjusters.dayOfWeekInMonth(ordinal, dayOfWeek)) as T

fun <T: Temporal> T.firstInMonth(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.firstInMonth(dayOfWeek)) as T
fun <T: Temporal> T.lastInMonth(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.lastInMonth(dayOfWeek)) as T
fun <T: Temporal> T.previous(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.previous(dayOfWeek)) as T
fun <T: Temporal> T.previousOrSame(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.previousOrSame(dayOfWeek)) as T
fun <T: Temporal> T.next(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.next(dayOfWeek)) as T
fun <T: Temporal> T.nextOrSame(dayOfWeek: DayOfWeek): T = with(TemporalAdjusters.nextOrSame(dayOfWeek)) as T

infix fun <T: Temporal> T.supports(temporalUnit: TemporalUnit): Boolean = isSupported(temporalUnit)

fun <T: TemporalAccessor> T.toInstant(): Instant = Instant.from(this)

/**
 * [Temporal] 을 Epoch 이후의 milli seconds 단위로 표현한 값 (기존 Date#time, Timestamp 와 같은 값을 나타낸다)
 */
fun <T: Temporal> T.toEpochMillis(): Long = when (this) {
    is Instant        -> toEpochMilli()
    is LocalDate      -> zonedDateTimeOf(year, monthValue, dayOfMonth).toEpochMillis()
    is LocalDateTime  -> toZonedDateTime(UtcOffset).toEpochMillis()
    is OffsetDateTime -> toInstant().toEpochMilli()
    //    is OffsetTime     -> toInstant().toEpochMilli()
    is ZonedDateTime  -> toInstant().toEpochMilli()
    else              ->
        if (isSupported(ChronoField.EPOCH_DAY) && isSupported(ChronoField.MILLI_OF_DAY)) {
            val days = getLong(ChronoField.EPOCH_DAY)
            val millis = getLong(ChronoField.MILLI_OF_DAY)
            days * MILLIS_IN_DAY + millis
        } else {
            0L
        }
}

fun <T: Temporal> T.toEpochDay(): Long = toEpochMillis() / (24 * 60 * 60 * 1000)

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.asTemporal(zoneId: ZoneId = SystemZoneId): T = when (this) {
    is Instant        -> Instant.ofEpochMilli(this.toEpochMillis()) as T
    is LocalDate      -> Instant.ofEpochMilli(this.toEpochMillis()).toLocalDate() as T
    is LocalDateTime  -> Instant.ofEpochMilli(this.toEpochMillis()).toLocalDateTime() as T
    is OffsetDateTime -> Instant.ofEpochMilli(this.toEpochMillis()).toOffsetDateTime(zoneId) as T
    is ZonedDateTime  -> Instant.ofEpochMilli(this.toEpochMillis()).toZonedDateTime(zoneId) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

fun <T: Temporal> T.startOf(chronoUnit: ChronoUnit): T = when (chronoUnit) {
    ChronoUnit.YEARS   -> startOfYear()
    ChronoUnit.MONTHS  -> startOfMonth()
    ChronoUnit.WEEKS   -> previousOrSame(DayOfWeek.MONDAY)
    ChronoUnit.DAYS    -> startOfDay()
    ChronoUnit.HOURS   -> startOfHour()
    ChronoUnit.MINUTES -> startOfMinute()
    ChronoUnit.SECONDS -> startOfSecond()
    ChronoUnit.MILLIS  -> startOfMillis()
    else               -> throw IllegalArgumentException("Unsupported ChronoUnit. chronoUnit=$chronoUnit")
}


@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfYear(): T = when (this) {
    is Instant        -> (startOfDay() as Instant).toZonedDateTime(UtcOffset).withDayOfYear(1).toInstant() as T
    is LocalDate      -> withDayOfYear(1).startOfDay() as T
    is LocalDateTime  -> withDayOfYear(1).startOfDay() as T
    is OffsetDateTime -> offsetDateTimeOf(year, 1, 1) as T
    is ZonedDateTime  -> zonedDateTimeOf(year, 1, 1) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfMonth(): T = when (this) {
    is Instant        -> (startOfDay() as Instant).toZonedDateTime(UtcOffset).withDayOfMonth(1).toInstant() as T
    is LocalDate      -> withDayOfMonth(1).startOfDay() as T
    is LocalDateTime  -> withDayOfMonth(1).startOfDay() as T
    is OffsetDateTime -> offsetDateTimeOf(year, monthValue, 1) as T
    is ZonedDateTime  -> zonedDateTimeOf(year, monthValue, 1) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfWeek(): T = when (this) {
    is Instant        -> (startOfDay() as Instant).toZonedDateTime(UtcOffset).startOfWeek() as T
    is LocalDate      -> (startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()) as T
    is LocalDateTime  -> (startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()) as T
    is OffsetDateTime -> (startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()) as T
    is ZonedDateTime  -> (startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfDay(): T = when (this) {
    is Instant        -> truncatedTo(ChronoUnit.DAYS) as T
    is LocalDate      -> this
    is LocalTime      -> this
    is LocalDateTime  -> truncatedTo(ChronoUnit.DAYS) as T
    is OffsetDateTime -> truncatedTo(ChronoUnit.DAYS) as T
    is ZonedDateTime  -> truncatedTo(ChronoUnit.DAYS) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfHour(): T = when (this) {
    is Instant        -> truncatedTo(ChronoUnit.HOURS) as T
    is LocalDateTime  -> truncatedTo(ChronoUnit.HOURS) as T
    is OffsetDateTime -> truncatedTo(ChronoUnit.HOURS) as T
    is ZonedDateTime  -> truncatedTo(ChronoUnit.HOURS) as T
    is LocalTime      -> truncatedTo(ChronoUnit.HOURS) as T
    is OffsetTime     -> truncatedTo(ChronoUnit.HOURS) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfMinute(): T = when (this) {
    is Instant        -> truncatedTo(ChronoUnit.MINUTES) as T
    is LocalDateTime  -> truncatedTo(ChronoUnit.MINUTES) as T
    is OffsetDateTime -> truncatedTo(ChronoUnit.MINUTES) as T
    is ZonedDateTime  -> truncatedTo(ChronoUnit.MINUTES) as T
    is LocalTime      -> truncatedTo(ChronoUnit.MINUTES) as T
    is OffsetTime     -> truncatedTo(ChronoUnit.MINUTES) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfSecond(): T = when (this) {
    is Instant        -> truncatedTo(ChronoUnit.SECONDS) as T
    is LocalDateTime  -> truncatedTo(ChronoUnit.SECONDS) as T
    is OffsetDateTime -> truncatedTo(ChronoUnit.SECONDS) as T
    is ZonedDateTime  -> truncatedTo(ChronoUnit.SECONDS) as T
    is LocalTime      -> truncatedTo(ChronoUnit.SECONDS) as T
    is OffsetTime     -> truncatedTo(ChronoUnit.SECONDS) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

@Suppress("UNCHECKED_CAST")
fun <T: Temporal> T.startOfMillis(): T = when (this) {
    is Instant        -> truncatedTo(ChronoUnit.MILLIS) as T
    is LocalDateTime  -> truncatedTo(ChronoUnit.MILLIS) as T
    is OffsetDateTime -> truncatedTo(ChronoUnit.MILLIS) as T
    is ZonedDateTime  -> truncatedTo(ChronoUnit.MILLIS) as T
    is LocalTime      -> truncatedTo(ChronoUnit.MILLIS) as T
    is OffsetTime     -> truncatedTo(ChronoUnit.MILLIS) as T
    else              -> error("Not supported class [${this.javaClass}]")
}

infix fun <T> T?.min(that: T?): T? where T: Temporal, T: Comparable<T> = when {
    this == null -> that
    that == null -> this
    this < that  -> this
    else         -> that
}

infix fun <T> T?.max(that: T?): T? where T: Temporal, T: Comparable<T> = when {
    this == null -> that
    that == null -> this
    this > that  -> this
    else         -> that
}
