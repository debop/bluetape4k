package io.bluetape4k.times

import io.bluetape4k.times.TimeSpec.SystemOffset
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

fun offsetTimeOf(
    hourOfDay: Int,
    minuteOfHour: Int = 0,
    secondOfMinute: Int = 0,
    nanoOfSeconds: Int = 0,
    offset: ZoneOffset = ZoneOffset.UTC,
): OffsetTime =
    OffsetTime.of(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSeconds, offset)

fun OffsetTime.toInstant(): Instant = Instant.from(this)
