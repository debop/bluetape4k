package io.bluetape4k.support

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

val Duration.nanosOfMillis: Int
    get() = (inWholeNanoseconds % 1_000_000).toInt()


fun Duration.sleep() {
    val finishInstant = Instant.now().plus(this.toJavaDuration())
    var remainingDuration = this
    do {
        Thread.sleep(remainingDuration.inWholeMilliseconds, remainingDuration.nanosOfMillis)
        remainingDuration = java.time.Duration.between(Instant.now(), finishInstant).toKotlinDuration()
    } while (!remainingDuration.isNegative())
}
