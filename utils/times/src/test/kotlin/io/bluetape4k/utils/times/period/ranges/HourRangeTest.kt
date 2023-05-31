package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.times.TimeSpec.HoursPerDay
import io.bluetape4k.utils.times.TimeSpec.MinNegativeDuration
import io.bluetape4k.utils.times.TimeSpec.MinutesPerHour
import io.bluetape4k.utils.times.hours
import io.bluetape4k.utils.times.minutes
import io.bluetape4k.utils.times.nowZonedDateTime
import io.bluetape4k.utils.times.period.AbstractPeriodTest
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfHour
import io.bluetape4k.utils.times.todayZonedDateTime
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test


class HourRangeTest : AbstractPeriodTest() {

    companion object : KLogging()

    @Test
    fun `simple construct`() {
        val now = nowZonedDateTime()
        val firstHour = now.startOfHour()
        val secondHour = firstHour + 1.hours()

        val hr = HourRange(now, TimeCalendar.EmptyOffset)

        hr.start shouldBeEqualTo firstHour
        hr.end shouldBeEqualTo secondHour
    }

    @Test
    fun `use default calendar`() {
        val today = todayZonedDateTime()

        repeat(HoursPerDay) { h ->
            val hr = HourRange(today + h.hours())

            hr.start shouldBeEqualTo today + h.hours()
            hr.end shouldBeEqualTo today + (h + 1).hours() + MinNegativeDuration
        }
    }

    @Test
    fun `add hour`() {
        val hr = HourRange()

        hr.prevHour().hourOfDay shouldBeEqualTo (hr.start - 1.hours()).hour
        hr.nextHour().hourOfDay shouldBeEqualTo (hr.start + 1.hours()).hour

        val emptyHr = HourRange(nowZonedDateTime(), TimeCalendar.EmptyOffset)

        emptyHr.addHours(0) shouldBeEqualTo emptyHr

        val prevHr = emptyHr.prevHour()
        prevHr.start shouldBeEqualTo emptyHr.start - 1.hours()
        prevHr.end shouldBeEqualTo emptyHr.end - 1.hours()

        val nextHr = emptyHr.nextHour()
        nextHr.start shouldBeEqualTo emptyHr.start + 1.hours()
        nextHr.end shouldBeEqualTo emptyHr.end + 1.hours()

        (-100..100).toList().parallelStream().forEach { h ->
            val hr1 = emptyHr.addHours(h)
            val hr2 = emptyHr.addHours(h)

            hr1 shouldBeEqualTo hr2
            hr1.hourOfDay shouldBeEqualTo hr2.hourOfDay
        }
    }

    @Test
    fun `generate minute range sequence`() {
        val hr = HourRange()
        val minuteSeq = hr.minuteSequence()

        minuteSeq.count() shouldBeEqualTo MinutesPerHour

        minuteSeq.forEachIndexed { i, m ->
            m.start shouldBeEqualTo hr.start + i.minutes()
            m.unmappedStart shouldBeEqualTo hr.start + i.minutes()

            m.end shouldBeEqualTo hr.start + (i + 1).minutes() + MinNegativeDuration
            m.unmappedEnd shouldBeEqualTo hr.start + (i + 1).minutes()
        }
    }
}
