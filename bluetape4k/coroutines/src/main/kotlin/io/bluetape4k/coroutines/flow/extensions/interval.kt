@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.time.delay
import java.time.Duration

fun <T> Flow<T>.interval(
    initialDelay: Duration,
    delay: Duration = Duration.ZERO,
): Flow<T> = flow {
    this@interval
        .onStart { delay(initialDelay.coerceAtLeast(Duration.ZERO)) }
        .onEach { delay.coerceAtLeast(Duration.ZERO) }
        .collect {
            emit(it)
        }
}

fun <T> Flow<T>.interval(
    initialDelayMillis: Long = 0L,
    delayMillis: Long = 0L,
): Flow<T> = flow {
    this@interval
        .onStart { delay(initialDelayMillis.coerceAtLeast(0)) }
        .onEach { delayMillis.coerceAtLeast(0) }
        .collect {
            emit(it)
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
        delay(delayMillis.coerceAtLeast(0))
    }
}
