package io.bluetape4k.utils.times.period

import java.time.ZonedDateTime

interface ITimePeriodMapper {

    fun mapStart(moment: ZonedDateTime): ZonedDateTime

    fun mapEnd(moment: ZonedDateTime): ZonedDateTime

    fun unmapStart(moment: ZonedDateTime): ZonedDateTime

    fun unmapEnd(moment: ZonedDateTime): ZonedDateTime

}
