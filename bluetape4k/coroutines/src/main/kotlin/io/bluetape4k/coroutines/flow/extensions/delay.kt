@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration

/**
 * Signal 0L after the given time passed
 */
fun flowOfDelay(initialDelay: Duration): Flow<Long> = delayedFlow(initialDelay)

fun flowOfDelay(initialDelayMillis: Long): Flow<Long> = delayedFlow(initialDelayMillis)

fun delayedFlow(delay: Duration): Flow<Long> = delayedFlow(delay.toMillis())

fun delayedFlow(delayMillis: Long): Flow<Long> = flow {
    delay(delayMillis.coerceAtLeast(0L))
    emit(0L)
}
