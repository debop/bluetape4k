package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowSkipUntil
import kotlinx.coroutines.flow.Flow
import java.time.Duration

fun <T, U> Flow<T>.skipUntil(notifier: Flow<U>): Flow<T> = FlowSkipUntil(this, notifier)

fun <T> Flow<T>.skipUntil(delay: Duration): Flow<T> = FlowSkipUntil(this, delayedFlow(delay))

fun <T> Flow<T>.skipUnitil(delayMillis: Long): Flow<T> = FlowSkipUntil(this, delayedFlow(delayMillis))
