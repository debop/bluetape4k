package io.bluetape4k.utils.times.range

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.add
import io.bluetape4k.utils.times.days
import io.bluetape4k.utils.times.hours
import io.bluetape4k.utils.times.localTimeOf
import io.bluetape4k.utils.times.offsetTimeOf
import io.bluetape4k.utils.times.range.coroutines.chunkedFlowHours
import io.bluetape4k.utils.times.range.coroutines.windowedFlowHours
import io.bluetape4k.utils.times.seconds
import io.bluetape4k.utils.times.startOfHour
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import kotlin.test.assertFailsWith


@Suppress("UNCHECKED_CAST")
abstract class TemporalOpenedRangeTest<T> where T : Temporal, T : Comparable<T> {

    companion object : KLogging()

    abstract val start: T
    open val duration: TemporalAmount = 5.hours()

    private val endExclusive: T by lazy { start.add(duration) }
    private val range: TemporalOpenedRange<T> by lazy { start until endExclusive }

    @Test
    fun `start gerater than endExclusive`() {
        assertFailsWith<AssertionError> {
            endExclusive until start
        }
    }

    @Test
    fun `empty range`() {
        val empty = start until start
        empty.isEmpty()
        empty shouldBeEqualTo TemporalOpenedRange.EMPTY
    }

    @Test
    fun `create by until`() {
        val range1 = range
        val range2 = start until endExclusive

        range1 shouldBeEqualTo range2
    }

    @Test
    fun `windowed with flow`() = runTest {
        val range = start.startOfHour() until (start.startOfHour() + 5.hours()) as T
        log.debug { "range=$range" }

        val windowed = range.windowedFlowHours(3, 1)
            .onEach { log.trace { "windowed $it" } }
            .toList()

        windowed.size shouldBeEqualTo 5
    }

    @Test
    fun `chunk ranges with flow`() = runTest {
        val range = start.startOfHour() until (start.startOfHour() + 5.hours()) as T
        log.debug { "range=$range" }

        val chunked = range.chunkedFlowHours(3)
            .onEach { log.trace { "chunked $it" } }
            .toList()

        chunked.size shouldBeEqualTo 2
    }
}

class InstantRangeExclusiveTest : TemporalOpenedRangeTest<Instant>() {
    override val start: Instant = Instant.now()
}

class LocalDateTimeRangeExclusiveTest : TemporalOpenedRangeTest<LocalDateTime>() {
    override val start: LocalDateTime = LocalDateTime.now()
}

class OffsetDateTimeRangeExclusiveTest : TemporalOpenedRangeTest<OffsetDateTime>() {
    override val start: OffsetDateTime = OffsetDateTime.now()
}

class ZonedDateTimeRangeExclusiveTest : TemporalOpenedRangeTest<ZonedDateTime>() {
    override val start: ZonedDateTime = ZonedDateTime.now()
}

class LocalTimeRangeExclusiveTest : TemporalOpenedRangeTest<LocalTime>() {
    override val start: LocalTime = localTimeOf(7, 12, 45)
    override val duration: TemporalAmount = 5.seconds()
}

class OffsetTimeRangeExclusiveTest : TemporalOpenedRangeTest<OffsetTime>() {
    override val start = offsetTimeOf(7, 12, 55)
    override val duration: TemporalAmount = 5.seconds()
}

@Disabled("LocalDate 는 지원하지 않습니다.")
class LocalDateRangeExclusiveTest : TemporalOpenedRangeTest<LocalDate>() {
    override val start: LocalDate = LocalDate.now()
    override val duration: TemporalAmount = 5.days()
}
