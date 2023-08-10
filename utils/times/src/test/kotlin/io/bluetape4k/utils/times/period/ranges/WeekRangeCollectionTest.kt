package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.nowZonedDateTime
import io.bluetape4k.utils.times.period.AbstractPeriodTest
import io.bluetape4k.utils.times.startOfWeek
import io.bluetape4k.utils.times.startOfWeekOfWeekyear
import io.bluetape4k.utils.times.todayZonedDateTime
import io.bluetape4k.utils.times.weekOfWeekyear
import io.bluetape4k.utils.times.weekyear
import io.bluetape4k.utils.times.zonedDateTimeOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class WeekRangeCollectionTest : AbstractPeriodTest() {

    companion object : KLogging()

    @ParameterizedTest(name = "single week collection. day={0}")
    @ValueSource(ints = [1, 15, 31])
    fun `single week collection`(day: Int) {
        val now = zonedDateTimeOf(2019, 12, day)
        val startYear = now.weekyear
        val startWeek = now.weekOfWeekyear
        val start = startOfWeekOfWeekyear(now.weekyear, now.weekOfWeekyear)
        log.trace { "weekyear=$startYear, weekOfWeekyear=$startWeek, start=$start" }

        val wrs = WeekRangeCollection(startYear, startWeek, 1)
        log.debug { "wrs=$wrs" }

        wrs.weekCount shouldBeEqualTo 1
        if (now.year == startYear) {
            wrs.startYear shouldBeEqualTo startYear
        } else {
            wrs.startYear shouldBeEqualTo startYear - 1
        }
        wrs.endYear shouldBeEqualTo startYear
        wrs.startWeekOfWeekyear shouldBeEqualTo startWeek
        wrs.endWeekOfWeekyear shouldBeEqualTo startWeek

        val weekSeq = wrs.weekSequence()
        weekSeq.count() shouldBeEqualTo 1
        weekSeq.first() shouldBeEqualTo WeekRange(startYear, startWeek)
    }

    @Test
    fun `week range collection with calendar`() {
        val startYear = 2018
        val startWeek = 22
        val weekCount = 5

        val wrs = WeekRangeCollection(startYear, startWeek, weekCount)

        wrs.weekCount shouldBeEqualTo weekCount
        wrs.startYear shouldBeEqualTo startYear
        wrs.startWeekOfWeekyear shouldBeEqualTo startWeek
        wrs.endYear shouldBeEqualTo startYear
        wrs.endWeekOfWeekyear shouldBeEqualTo startWeek + weekCount - 1
    }

    @Test
    fun `various weekCount`() {
        val weekCounts = listOf(1, 6, 48, 180, 365)

        val now = nowZonedDateTime()
        val today = todayZonedDateTime()

        weekCounts.parallelStream().forEach { weekCount ->
            val wrs = WeekRangeCollection(now, weekCount)

            val startTime = wrs.calendar.mapStart(today.startOfWeek())
            val endTime = wrs.calendar.mapEnd(startTime.plusWeeks(weekCount.toLong()))

            wrs.start shouldBeEqualTo startTime
            wrs.end shouldBeEqualTo endTime

            val wrSeq = wrs.weekSequence()
            wrSeq.count() shouldBeEqualTo weekCount

            runBlocking {
                val tasks = wrSeq.mapIndexed { w, wr ->
                    async {
                        wr.start shouldBeEqualTo startTime.plusWeeks(w.toLong())
                        wr.end shouldBeEqualTo wr.calendar.mapEnd(startTime.plusWeeks(w + 1L))

                        wr.unmappedStart shouldBeEqualTo startTime.plusWeeks(w.toLong())
                        wr.unmappedEnd shouldBeEqualTo startTime.plusWeeks(w + 1L)

                        wr shouldBeEqualTo WeekRange(wrs.start.plusWeeks(w.toLong()))
                        val afterWeek = now.startOfWeek().plusWeeks(w.toLong())
                        wr shouldBeEqualTo WeekRange(afterWeek)
                    }
                }.toList()
                tasks.awaitAll()
            }
        }
    }

    @Test
    fun `various weekCount in coroutines`() = runTest {
        val weekCounts = listOf(1, 6, 48, 180, 365)

        val now = nowZonedDateTime()
        val today = todayZonedDateTime()

        MultiJobTester()
            .numJobs(8)
            .roundsPerJob(5)
            .add {
                weekCounts.forEach { weekCount ->
                    val wrs = WeekRangeCollection(now, weekCount)

                    val startTime = wrs.calendar.mapStart(today.startOfWeek())
                    val endTime = wrs.calendar.mapEnd(startTime.plusWeeks(weekCount.toLong()))

                    wrs.start shouldBeEqualTo startTime
                    wrs.end shouldBeEqualTo endTime

                    val wrSeq = wrs.weekSequence()
                    wrSeq.count() shouldBeEqualTo weekCount

                    val tasks = wrSeq.mapIndexed { w, wr ->
                        async {
                            wr.start shouldBeEqualTo startTime.plusWeeks(w.toLong())
                            wr.end shouldBeEqualTo wr.calendar.mapEnd(startTime.plusWeeks(w + 1L))

                            wr.unmappedStart shouldBeEqualTo startTime.plusWeeks(w.toLong())
                            wr.unmappedEnd shouldBeEqualTo startTime.plusWeeks(w + 1L)

                            wr shouldBeEqualTo WeekRange(wrs.start.plusWeeks(w.toLong()))
                            val afterWeek = now.startOfWeek().plusWeeks(w.toLong())
                            wr shouldBeEqualTo WeekRange(afterWeek)
                        }
                    }.toList()
                    tasks.awaitAll()
                }
            }
            .run()
    }
}
