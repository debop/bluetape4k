package io.bluetape4k.times.period.timelines

import io.bluetape4k.times.period.ITimePeriod
import io.bluetape4k.times.period.ITimePeriodCollection
import io.bluetape4k.times.period.ITimePeriodContainer
import io.bluetape4k.times.period.ITimePeriodMapper

open class TimeGapCalculator<T: ITimePeriod>(val mapper: ITimePeriodMapper? = null) {

    fun gaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod? = null): ITimePeriodCollection =
        TimeLine<T>(excludePeriods, limits, mapper).calculateGaps()

}
