package io.bluetape4k.times.period.timelines

import io.bluetape4k.core.ValueObject
import io.bluetape4k.times.period.ITimePeriodCollection
import java.time.ZonedDateTime

/**
 * ITimeLineMoment
 */
interface ITimeLineMoment: ValueObject, Comparable<ITimeLineMoment> {

    val moment: ZonedDateTime

    val periods: ITimePeriodCollection

    val startCount: Long

    val endCount: Long

}
