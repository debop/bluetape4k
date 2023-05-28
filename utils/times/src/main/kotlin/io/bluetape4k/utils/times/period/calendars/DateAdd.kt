package io.bluetape4k.utils.times.period.calendars

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.MaxDuration
import io.bluetape4k.utils.times.isNotNegative
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.SeekBoundaryMode
import io.bluetape4k.utils.times.period.SeekDirection
import io.bluetape4k.utils.times.period.TimePeriodCollection
import io.bluetape4k.utils.times.period.TimeRange
import io.bluetape4k.utils.times.period.hasInsideWith
import io.bluetape4k.utils.times.period.timelines.TimeGapCalculator
import io.bluetape4k.utils.times.period.timelines.TimePeriodCombiner
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import java.time.Duration
import java.time.ZonedDateTime


/**
 * DateAdd
 */
open class DateAdd protected constructor() {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(): DateAdd = DateAdd()
    }

    open var includePeriods: TimePeriodCollection = TimePeriodCollection()
        protected set

    open var excludePeriods: TimePeriodCollection = TimePeriodCollection()
        protected set

    /**
     * 시작 일자로부터 offset 기간이 지난 일자를 계산합니다. (기간에 포함될 기간과 제외할 기간을 명시적으로 지정해 놓을 수 있습니다.)
     *
     * @param start        시작 일자
     * @param offset       기간
     * @param seekBoundary 마지막 일자 포함 여부
     * @return 시작 일자로부터 offset 기간 이후의 일자
     */
    open suspend fun add(
        start: ZonedDateTime,
        offset: Duration,
        seekBoundary: SeekBoundaryMode = SeekBoundaryMode.NEXT,
    ): ZonedDateTime? {
        log.trace { "Add start=$start, offset=$offset, seekBoundary=$seekBoundary" }

        // 예외 조건이 없으면 단순 계산으로 처리
        if (includePeriods.isEmpty() && excludePeriods.isEmpty()) {
            return start + offset
        }

        val (end, remaining) = when {
            offset.isNegative -> calculateEnd(start, offset.negated(), SeekDirection.BACKWARD, seekBoundary)
            else              -> calculateEnd(start, offset, SeekDirection.FORWARD, seekBoundary)
        }

        log.trace { "Add results. end=$end, remaining=$remaining" }
        return end
    }

    /**
     * 시작 일자로부터 offset 기간 이전의 일자를 계산합니다. (기간에 포함될 기간과 제외할 기간을 명시적으로 지정해 놓을 수 있습니다.)
     *
     * @param start        시작 일자
     * @param offset       기간
     * @param seekBoundary 마지막 일자 포함 여부
     * @return 시작 일자로부터 offset 기간 이전의 일자
     */
    open suspend fun subtract(
        start: ZonedDateTime,
        offset: Duration,
        seekBoundary: SeekBoundaryMode = SeekBoundaryMode.NEXT,
    ): ZonedDateTime? {
        log.trace { "Substract start=$start, offset=$offset, seekBoundary=$seekBoundary" }

        // 예외 조건이 없으면 단순 계산으로 처리
        if (includePeriods.isEmpty() && excludePeriods.isEmpty()) {
            return start - offset
        }

        val (end, remaining) = when {
            offset.isNegative -> calculateEnd(start, offset.negated(), SeekDirection.FORWARD, seekBoundary)
            else              -> calculateEnd(start, offset, SeekDirection.BACKWARD, seekBoundary)
        }

        log.trace { "Substract results. end=$end, remaining=$remaining" }
        return end
    }

    @JvmOverloads
    protected open suspend fun calculateEnd(
        start: ZonedDateTime,
        offset: Duration?,
        seekDir: SeekDirection,
        seekBoundary: SeekBoundaryMode = SeekBoundaryMode.NEXT,
    ): Pair<ZonedDateTime?, Duration?> {
        check(offset?.isNotNegative ?: false) { "offset 값은 0 이상이어야 합니다." }
        log.trace { "calculateEnd start=$start, offset=$offset, seekDir=$seekDir, seekBoundary=$seekBoundary" }

        val searchPeriods = TimePeriodCollection.ofAll(includePeriods.periods)
        if (searchPeriods.isEmpty()) {
            searchPeriods += TimeRange.AnyTime
        }

        // available periods
        var availablePeriods = getAvailablePeriods(searchPeriods)
        if (availablePeriods.isEmpty()) {
            return Pair(null, offset)
        }

        val periodCombiner = TimePeriodCombiner<TimeRange>()
        availablePeriods = periodCombiner.combinePeriods(availablePeriods)

        val startPeriod = when {
            seekDir.isForward -> findNextPeriod(start, availablePeriods)
            else              -> findPrevPeriod(start, availablePeriods)
        }

        // 첫 시작 기간이 없다면 중단한다.
        if (startPeriod.first == null) {
            log.trace { "startPeriod.first is null" }
            return Pair(null, offset)
        }

        if (offset == Duration.ZERO) {
            log.trace { "offset is zero, return Pair(${startPeriod.second}, $offset)" }
            return Pair(startPeriod.second, offset)
        }

        log.trace { "startPeriod=$startPeriod, offset=$offset" }

        return when {
            seekDir.isForward -> findPeriodForward(
                availablePeriods,
                offset,
                startPeriod.first,
                startPeriod.second,
                seekBoundary
            )

            else              -> findPeriodBackward(
                availablePeriods,
                offset,
                startPeriod.first,
                startPeriod.second,
                seekBoundary
            )
        }
    }

    private suspend fun getAvailablePeriods(searchPeriods: ITimePeriodCollection): ITimePeriodCollection {
        val availablePeriods = TimePeriodCollection()

        if (excludePeriods.isEmpty()) {
            availablePeriods.addAll(searchPeriods)
        } else {
            val gapCalculator = TimeGapCalculator<TimeRange>()

            searchPeriods
                .asFlow()
                .collect { p ->
                    if (excludePeriods.hasOverlapPeriods(p)) {
                        gapCalculator.gaps(excludePeriods, p).forEach { gap -> availablePeriods += gap }
                    } else {
                        availablePeriods += p
                    }
                }
        }

        log.trace { "availablePeriods=$availablePeriods" }
        return availablePeriods
    }

    @Suppress("NAME_SHADOWING")
    private fun findPeriodForward(
        availablePeriods: ITimePeriodCollection,
        remaining: Duration?,
        startPeriod: ITimePeriod?,
        seekMoment: ZonedDateTime,
        seekBoundary: SeekBoundaryMode,
    ): Pair<ZonedDateTime?, Duration?> {
        log.trace { "find period forward remaining=$remaining" }

        var seekMoment = seekMoment
        var remaining = remaining

        val startIndex = availablePeriods.indexOf(startPeriod)
        val length = availablePeriods.size

        for (i in startIndex until length) {
            val gap = availablePeriods[i]
            val gapRemaining = Duration.between(seekMoment, gap.end)

            log.trace { "gap=$gap, gapRemaining=$gapRemaining, remaining=$remaining, seekMoment=$seekMoment" }

            val isTargetPeriod = when {
                seekBoundary.isFill -> gapRemaining >= remaining
                else                -> gapRemaining > remaining
            }

            if (isTargetPeriod) {
                val foundMoment = seekMoment + remaining
                log.trace { "find datetime=$foundMoment" }
                return Pair(foundMoment, null)
            }

            remaining = remaining?.minus(gapRemaining)

            if (i < length - 1) {
                seekMoment = availablePeriods[i + 1].start
            }
        }

        log.trace { "해당일자를 찾지 못했습니다. remaining=${remaining.toString()}" }
        return Pair(null, remaining)
    }

    @Suppress("NAME_SHADOWING")
    private fun findPeriodBackward(
        availablePeriods: ITimePeriodCollection,
        remaining: Duration?,
        startPeriod: ITimePeriod?,
        seekMoment: ZonedDateTime,
        seekBoundary: SeekBoundaryMode,
    ): Pair<ZonedDateTime?, Duration?> {
        log.trace { "find period backward remaining=$remaining" }

        var seekMoment = seekMoment
        var remaining = remaining

        val startIndex = availablePeriods.indexOf(startPeriod)
        // val length = availablePeriods.size

        (startIndex downTo 0)
            .forEach { i ->
                val gap = availablePeriods[i]
                val gapRemaining = Duration.between(gap.start, seekMoment)

                log.trace { "gap=$gap, gapRemaining=$gapRemaining, remaining=$remaining, seekMoment=$seekMoment" }

                val isTargetPeriod = when {
                    seekBoundary.isFill -> gapRemaining >= remaining
                    else                -> gapRemaining > remaining
                }

                if (isTargetPeriod) {
                    val foundMoment = seekMoment - remaining
                    log.trace { "find datetime=$foundMoment" }
                    return Pair(foundMoment, null)
                }

                remaining = remaining?.minus(gapRemaining)

                if (i > 0) {
                    seekMoment = availablePeriods[i - 1].end
                }
            }

        log.trace { "해당일자를 찾지 못했습니다. remaining=$remaining" }
        return Pair(null, remaining)
    }

    private suspend fun findNextPeriod(
        start: ZonedDateTime,
        periods: Collection<ITimePeriod>,
    ): Pair<ITimePeriod?, ZonedDateTime> {
        var nearest: ITimePeriod? = null
        var moment = start
        var diff = MaxDuration

        log.trace { "find next period. start=$start, periods=$periods" }

        var pair: Pair<ITimePeriod?, ZonedDateTime>? = null
        periods
            .asFlow()
            .filter { it.end >= start }
            .firstOrNull { period ->
                // start가 기간에 속한다면
                if (period.hasInsideWith(start)) {
                    pair = Pair(period, start)
                    return@firstOrNull true
                }

                // 근처 값이 아니라면 포기
                val periodToMoment = Duration.between(start, period.end)
                log.trace { "diff=$diff, periodToMoment=$periodToMoment" }
                if (periodToMoment < diff) {
                    diff = periodToMoment
                    nearest = period
                    moment = period.start
                }
                false
            }
        return pair ?: Pair(nearest, moment)
    }

    private suspend fun findPrevPeriod(
        start: ZonedDateTime,
        periods: Collection<ITimePeriod>,
    ): Pair<ITimePeriod?, ZonedDateTime> {
        var nearest: ITimePeriod? = null
        var moment = start
        var diff = MaxDuration

        log.trace { "find prev period. start=$start, periods=$periods" }

        var pair: Pair<ITimePeriod?, ZonedDateTime>? = null
        periods.asFlow()
            .filter { it.start <= start }
            .firstOrNull { period ->
                // start가 기간에 속한다면
                if (period.hasInsideWith(start)) {
                    pair = Pair(period, start)
                    return@firstOrNull true
                }

                // 근처 값이 아니라면 포기
                val periodToMoment = Duration.between(start, period.end)
                if (periodToMoment < diff) {
                    diff = periodToMoment
                    nearest = period
                    moment = period.end
                }
                false
            }

        return pair ?: Pair(nearest, moment)
    }
}
