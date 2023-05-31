package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.times.TimeSpec.HoursPerDay
import io.bluetape4k.utils.times.TimeSpec.MinNegativeDuration
import io.bluetape4k.utils.times.days
import io.bluetape4k.utils.times.hours
import io.bluetape4k.utils.times.nowZonedDateTime
import io.bluetape4k.utils.times.period.AbstractPeriodTest
import io.bluetape4k.utils.times.startOfDay
import io.bluetape4k.utils.times.todayZonedDateTime
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue


class DayRangeCollectionTest : AbstractPeriodTest() {

    companion object : KLogging()

    @Test
    fun `single day`() {
        val start = todayZonedDateTime()
        val days = DayRangeCollection(start, 1)

        days.dayCount shouldBeEqualTo 1
        days.start shouldBeEqualTo start
        days.end shouldBeEqualTo start.plusDays(1) + MinNegativeDuration

        val daySeq = days.daySequence()
        daySeq.count() shouldBeEqualTo 1
        daySeq.first() shouldBeEqualTo DayRange(start)
    }

    @Test
    fun `multiple days`() {
        val dayCount = 5
        val start = todayZonedDateTime()

        val days = DayRangeCollection(start, dayCount)

        days.start shouldBeEqualTo start
        days.end shouldBeEqualTo start + dayCount.days() + MinNegativeDuration

        val daySeq = days.daySequence()
        daySeq.count() shouldBeEqualTo dayCount

        daySeq.forEachIndexed { index, dr ->
            assertTrue { dr.isSamePeriod(DayRange(start + index.days())) }
        }
    }

    @Test
    fun `calendar hours`() {
        val dayCounts = listOf(1, 6, 48, 180, 480)

        dayCounts.parallelStream().forEach { dayCount ->

            val now = nowZonedDateTime()
            val days = DayRangeCollection(now, dayCount)

            val startTime = now.startOfDay() + days.calendar.startOffset
            val endTime = startTime + dayCount.days() + days.calendar.endOffset

            days.start shouldBeEqualTo startTime
            days.end shouldBeEqualTo endTime

            days.dayCount shouldBeEqualTo dayCount

            val hourSeq = days.hourSequence()

            hourSeq.count() shouldBeEqualTo dayCount * HoursPerDay
            hourSeq.forEachIndexed { i, hour ->
                hour.start shouldBeEqualTo startTime.plusHours(i.toLong())
                hour.end shouldBeEqualTo days.calendar.mapEnd(startTime.plusHours(i + 1L))
                hour.isSamePeriod(HourRange(days.start + i.hours())).shouldBeTrue()
            }
        }
    }
}
