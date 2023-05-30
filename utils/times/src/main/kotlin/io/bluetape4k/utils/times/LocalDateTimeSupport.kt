package io.bluetape4k.utils.times

import io.bluetape4k.utils.times.TimeSpec.SystemOffset
import io.bluetape4k.utils.times.TimeSpec.UtcOffset
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


@JvmOverloads
fun localDateTimeOf(
    year: Int,
    monthOfYear: Int = 1,
    dayOfMonth: Int = 1,
    hourOfDay: Int = 0,
    minuteOfHour: Int = 0,
    secondOfMinute: Int = 0,
    milliOfSecond: Int = 0,
): LocalDateTime =
    LocalDateTime.of(
        year,
        monthOfYear,
        dayOfMonth,
        hourOfDay,
        minuteOfHour,
        secondOfMinute,
        milliOfSecond.millisToNanos()
    )

fun LocalDateTime.toDate(): Date = Date.from(toInstant())
fun LocalDateTime.toInstant(): Instant = toZonedDateTime(UtcOffset).toInstant()


@JvmOverloads
fun LocalDateTime.toOffsetDateTime(offset: ZoneOffset = SystemOffset) = OffsetDateTime.of(this, offset)

@JvmOverloads
fun LocalDateTime.toZonedDateTime(offset: ZoneOffset = SystemOffset) = ZonedDateTime.of(this, offset)

//fun LocalDateTime.startOfYear(): LocalDateTime = this.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS)
//fun LocalDateTime.startOfMonth(): LocalDateTime = this.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
//fun LocalDateTime.startOfWeek(): LocalDateTime = startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()
//fun LocalDateTime.startOfDay(): LocalDateTime = this.truncatedTo(ChronoUnit.DAYS)


@JvmOverloads
fun localDateOf(
    year: Int,
    monthOfYear: Int = 1,
    dayOfMonth: Int = 1,
): LocalDate = LocalDate.of(year, monthOfYear, dayOfMonth)

@JvmOverloads
fun localDateOf(
    year: Int,
    month: Month,
    dayOfMonth: Int = 1,
): LocalDate = LocalDate.of(year, month, dayOfMonth)

fun LocalDate.toDate(): Date = Date.from(toInstant())
fun LocalDate.toInstant(): Instant = Instant.ofEpochMilli(toEpochMillis())

//fun LocalDate.startOfMonth(): LocalDate = withDayOfMonth(1)
// fun LocalDate.startOfWeek(): LocalDate = this - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()


fun LocalDate.between(endExclusive: LocalDate): Period = Period.between(this, endExclusive)

@JvmOverloads
fun localTimeOf(
    hourOfDay: Int,
    minuteOfHour: Int = 0,
    secondOfMinute: Int = 0,
    milliOfSecond: Int = 0,
): LocalTime =
    LocalTime.of(hourOfDay, minuteOfHour, secondOfMinute, milliOfSecond.millisToNanos())

fun LocalTime.toInstant(): Instant = Instant.from(this)
