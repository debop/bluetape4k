package io.bluetape4k.utils.jwt.utils

import java.util.*

val Date.epochSeconds: Long
    get() = this.time / 1000L

fun dateOfEpochSeconds(epochSeconds: Long): Date =
    Date(epochSeconds * 1000L)

fun Long.millisToSeconds(): Long = this / 1000L
