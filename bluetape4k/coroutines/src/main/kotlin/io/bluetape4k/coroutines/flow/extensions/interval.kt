@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> Flow<T>.interval(
    initialDelay: Duration,
    delay: Duration = Duration.ZERO,
): Flow<T> = channelFlow {
    this@interval
        .onStart { delay(initialDelay.coerceAtLeast(Duration.ZERO)) }
        .onEach { delay(delay.coerceAtLeast(Duration.ZERO)) }
        .collect {
            send(it)
        }
}

fun <T> Flow<T>.interval(
    initialDelayMillis: Long = 0L,
    delayMillis: Long = 0L,
): Flow<T> = channelFlow {
    this@interval
        .onStart { delay(initialDelayMillis.coerceAtLeast(0).milliseconds) }
        .onEach { delay(delayMillis.coerceAtLeast(0).milliseconds) }
        .collect {
            send(it)
        }
}

fun intervalFlowOf(initialDelay: Duration, delay: Duration): Flow<Long> = flow {
    delay(initialDelay)
    val sequencer = atomic(0L)
    while (true) {
        emit(sequencer.getAndIncrement())
        delay(delay)
    }
}

fun intervalFlowOf(initialDelayMillis: Long, delayMillis: Long): Flow<Long> = flow {
    delay(initialDelayMillis.coerceAtLeast(0))
    val sequencer = atomic(0L)
    while (true) {
        emit(sequencer.getAndIncrement())
        delay(delayMillis.coerceAtLeast(0).milliseconds)
    }
}
