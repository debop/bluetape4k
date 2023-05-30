package io.bluetape4k.utils.times

import io.bluetape4k.utils.times.TimeSpec.MILLIS_IN_DAY
import java.sql.Timestamp
import java.time.Duration
import java.time.Period
import java.util.*

fun dateOf(epochMillis: Long = System.currentTimeMillis()): Date = Date(epochMillis)

operator fun Date.plus(that: Date): Date = Date(this.time + that.time)
operator fun Date.plus(millis: Long): Date = Date(this.time + millis)
operator fun Date.plus(duration: Duration): Date = Date(this.time + duration.toMillis())
operator fun Date.plus(period: Period): Date = Date(this.time + period.days * MILLIS_IN_DAY)

operator fun Date.minus(that: Date): Date = Date(this.time - that.time)
operator fun Date.minus(millis: Long): Date = Date(this.time - millis)
operator fun Date.minus(duration: Duration): Date = Date(this.time - duration.toMillis())
operator fun Date.minus(period: Period): Date = Date(this.time - period.days * MILLIS_IN_DAY)

operator fun Timestamp.plus(that: Timestamp): Timestamp = Timestamp(this.time + that.time)
operator fun Timestamp.plus(millis: Long): Timestamp = Timestamp(this.time + millis)
operator fun Timestamp.plus(duration: Duration): Timestamp = Timestamp(this.time + duration.toMillis())
operator fun Timestamp.plus(period: Period): Timestamp = Timestamp(this.time + period.days * MILLIS_IN_DAY)

operator fun Timestamp.minus(that: Timestamp): Timestamp = Timestamp(this.time - that.time)
operator fun Timestamp.minus(millis: Long): Timestamp = Timestamp(this.time - millis)
operator fun Timestamp.minus(duration: Duration): Timestamp = Timestamp(this.time - duration.toMillis())
operator fun Timestamp.minus(period: Period): Timestamp = Timestamp(this.time - period.days * MILLIS_IN_DAY)
