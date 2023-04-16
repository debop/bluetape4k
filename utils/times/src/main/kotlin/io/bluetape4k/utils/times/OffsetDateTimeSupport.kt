package io.bluetape4k.utils.times

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset

fun offsetDateTimeOf(
    year: Int,
    monthOfYear: Int = 1,
    dayOfMonth: Int = 1,
    hourOfDay: Int = 0,
    minuteOfHour: Int = 0,
    secondOfMinute: Int = 0,
    milliOfSecond: Int = 0,
    offset: ZoneOffset = SystemOffset,
): OffsetDateTime =
    OffsetDateTime.of(
        year,
        monthOfYear,
        dayOfMonth,
        hourOfDay,
        minuteOfHour,
        secondOfMinute,
        milliOfSecond.millisToNanos(),
        offset
    )

fun offsetDateTimeOf(
    localDate: LocalDate = LocalDate.ofEpochDay(0),
    localTime: LocalTime = LocalTime.ofSecondOfDay(0),
    offset: ZoneOffset = SystemOffset,
): OffsetDateTime =
    OffsetDateTime.of(localDate, localTime, offset)

@Deprecated("어디다 써야 할지 ???")
fun OffsetDateTime.toUtcInstant(): Instant = Instant.ofEpochSecond(this.toEpochSecond())

//fun OffsetDateTime.startOfYear(): OffsetDateTime = withDayOfYear(1)
//fun OffsetDateTime.startOfMonth(): OffsetDateTime = withDayOfMonth(1)
//fun OffsetDateTime.startOfWeek(): OffsetDateTime = startOfDay() - (dayOfWeek.value - DayOfWeek.MONDAY.value).days()
//fun OffsetDateTime.startOfDay(): OffsetDateTime = this.truncatedTo(ChronoUnit.DAYS)

fun offsetTimeOf(
    hourOfDay: Int,
    minuteOfHour: Int = 0,
    secondOfMinute: Int = 0,
    nanoOfSeconds: Int = 0,
    offset: ZoneOffset = ZoneOffset.UTC,
): OffsetTime =
    OffsetTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSeconds, offset)

fun OffsetTime.toInstant(): Instant = Instant.from(this)
