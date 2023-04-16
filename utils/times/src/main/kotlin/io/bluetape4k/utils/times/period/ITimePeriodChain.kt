package io.bluetape4k.utils.times.period

import java.time.Duration
import java.time.ZonedDateTime

interface ITimePeriodChain: ITimePeriodContainer {

    fun headOrNull(): ITimePeriod? = periods.firstOrNull()

    fun lastOrNull(): ITimePeriod? = periods.lastOrNull()

    /**
     * [moment] 이전에 빈 기간이 있는지 확인한다. 없으면 예외를 발생시킨다.
     */
    fun assertSpaceBefore(moment: ZonedDateTime, duration: Duration)

    /**
     * [moment] 이후에 빈 기간이 있는지 확인한다. 없으면 예외를 발생시킨다.
     */
    fun assertSpaceAfter(moment: ZonedDateTime, duration: Duration)
}
