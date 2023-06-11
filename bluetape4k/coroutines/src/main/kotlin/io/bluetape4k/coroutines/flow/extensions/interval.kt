@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.time.delay
import java.time.Duration

fun <T> Flow<T>.interval(initialDelay: Duration, delay: Duration): Flow<T> = channelFlow {
    delay(initialDelay)
    collect {
        send(it)
        delay(delay)
    }
}

fun <T> Flow<T>.interval(initialDelayMillis: Long, delayMillis: Long): Flow<T> = channelFlow {
    delay(initialDelayMillis.coerceAtLeast(0))
    collect {
        send(it)
        delay(delayMillis.coerceAtLeast(0))
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
