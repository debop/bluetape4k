package io.bluetape4k.utils.times

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.times.TimeSpec.UtcOffset
import io.bluetape4k.utils.times.TimeSpec.UtcZoneId
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.DayOfWeek
import java.time.Instant
import java.time.Month
import java.time.ZonedDateTime
import kotlin.math.absoluteValue

@RandomizedTest
class InstantSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @ParameterizedTest(name = "start of year for instant: {0}")
    @ValueSource(longs = [1980, 2002, 2019, 2020, 2049, 2999])
    fun `start of year for instant`(year: Long) {
        val instant = Instant.now()

        val start = instant.startOfYear()
        val utcStart = start.toZonedDateTime(UtcOffset).plusYears(year)

        log.debug { "start=$start, utcStart=$utcStart" }

        utcStart.monthValue shouldBeEqualTo 1
        utcStart.dayOfMonth shouldBeEqualTo 1
        verifyStartOfDay(utcStart)
    }

    @ParameterizedTest(name = "start of month for instant: {0}")
    @EnumSource(Month::class)
    fun `start of month for instant`(month: Month) {
        val start = nowLocalDateTime().withMonth(month.value).withDayOfMonth(5).startOfMonth()
        val utcStart = start.toZonedDateTime()

        log.debug { "utcStart=$utcStart" }

        utcStart.month shouldBeEqualTo month
        utcStart.dayOfMonth shouldBeEqualTo 1
        verifyStartOfDay(utcStart)
    }

    @ParameterizedTest(name = "start of week for instant: {0}")
    @EnumSource(DayOfWeek::class)
    fun `start of week for instant`(dayOfWeek: DayOfWeek) {

        val start = (nowLocalDateTime(UtcZoneId).startOfYear() + dayOfWeek.value.days()).startOfWeek()
        val utcStart = start.toZonedDateTime(UtcOffset)

        log.debug { "utcStart=$utcStart" }

        utcStart.dayOfWeek shouldBeEqualTo DayOfWeek.MONDAY
        verifyStartOfDay(utcStart)
    }

    @Test
    fun `start of day for instant`() {
        val instant = nowInstant()

        val start = instant.startOfDay()
        val utcStart = start.toZonedDateTime(UtcZoneId)

        utcStart.dayOfMonth shouldBeEqualTo start.toLocalDateTime(UtcZoneId).dayOfMonth
        verifyStartOfDay(utcStart)
    }

    private fun verifyStartOfDay(utcStart: ZonedDateTime) {
        utcStart.hour shouldBeEqualTo 0
        utcStart.minute shouldBeEqualTo 0
        utcStart.second shouldBeEqualTo 0
        utcStart.nano shouldBeEqualTo 0
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `start of hour for instant`(@RandomValue hour: Int) {
        val instant = nowInstant()

        val start = instant.startOfHour() + hour.absoluteValue.hours()
        val utcStart = start.toZonedDateTime(UtcZoneId)

        utcStart.hour shouldBeEqualTo start.toLocalDateTime(UtcZoneId).hour
        utcStart.minute shouldBeEqualTo 0
        utcStart.second shouldBeEqualTo 0
        utcStart.nano shouldBeEqualTo 0
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `start of minute for instant`(@RandomValue minute: Int) {
        val instant = nowInstant()

        val start = instant.startOfHour() + minute.absoluteValue.minutes()
        val utcStart = start.toZonedDateTime(UtcZoneId)

        utcStart.minute shouldBeEqualTo start.toLocalDateTime(UtcZoneId).minute
        utcStart.second shouldBeEqualTo 0
        utcStart.nano shouldBeEqualTo 0
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `start of second for instant`(@RandomValue second: Int) {
        val instant = nowInstant()

        val start = instant.startOfHour() + second.absoluteValue.seconds()
        val utcStart = start.toZonedDateTime(UtcZoneId)

        utcStart.second shouldBeEqualTo start.toLocalDateTime(UtcZoneId).second
        utcStart.nano shouldBeEqualTo 0
    }

}
