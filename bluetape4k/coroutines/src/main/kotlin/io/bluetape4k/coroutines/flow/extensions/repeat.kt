@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")
@file:Suppress("NOTHING_TO_INLINE")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun <T> Flow<T>.repeat(): Flow<T> =
    repeatInternal(this, 0, true, noDelayFunction())

fun <T> Flow<T>.repeat(duration: Duration): Flow<T> =
    repeatInternal(this, 0, true, fixedDelayFunction(duration))

fun <T> Flow<T>.repeat(durationFunc: suspend (index: Int) -> Duration): Flow<T> =
    repeatInternal(this, 0, true, delayFunction(durationFunc))


fun <T> Flow<T>.repeat(count: Int): Flow<T> {
    return repeatInternal(this, count, false, noDelayFunction())
}

fun <T> Flow<T>.repeat(count: Int, duration: Duration): Flow<T> {
    return repeatInternal(this, count, false, fixedDelayFunction(duration))
}

fun <T> Flow<T>.repeat(count: Int, durationFunc: suspend (index: Int) -> Duration): Flow<T> {
    return repeatInternal(this, count, false, delayFunction(durationFunc))
}

// --------------- INTERNAL -------------------

private typealias DelayDurationFunction = suspend (count: Int) -> Duration

private inline fun noDelayFunction(): DelayDurationFunction? = null
private inline fun fixedDelayFunction(duration: Duration): DelayDurationFunction? {
    return if (duration.isZeroOrNegative()) {
        noDelayFunction()
    } else {
        FixedDelayDurationFunction(duration)
    }
}

private inline fun delayFunction(noinline durationFunc: DelayDurationFunction): DelayDurationFunction = durationFunc

private inline fun Duration.isZeroOrNegative(): Boolean =
    this == Duration.ZERO || isNegative()

private class FixedDelayDurationFunction(val duration: Duration): DelayDurationFunction {
    override suspend fun invoke(count: Int): Duration = duration
}

private fun <T> repeatInternal(
    flow: Flow<T>,
    count: Int,
    infinite: Boolean,
    durationFunc: DelayDurationFunction?,
): Flow<T> = when {
    infinite   -> repeatIndefinitely(flow, durationFunc)
    count <= 0 -> emptyFlow()
    else       -> repeatAtMostCount(flow, count, durationFunc)
}

private fun <T> repeatIndefinitely(
    flow: Flow<T>,
    durationFunc: DelayDurationFunction?,
): Flow<T> = when (durationFunc) {
    null                          ->
        flow {
            while (true) {
                emitAll(flow)
            }
        }

    is FixedDelayDurationFunction ->
        flow {
            while (true) {
                emitAll(flow)
                delay(durationFunc.duration)
            }
        }

    else                          ->
        flow {
            var soFar = 1
            while (true) {
                emitAll(flow)
                delay(durationFunc(soFar++))
            }
        }
}

private fun <T> repeatAtMostCount(
    flow: Flow<T>,
    count: Int,
    durationFunc: DelayDurationFunction?,
): Flow<T> = when (durationFunc) {
    null                          ->
        flow {
            repeat(count) {
                emitAll(flow)
            }
        }

    is FixedDelayDurationFunction ->
        flow {
            repeat(count) {
                emitAll(flow)
                delay(durationFunc.duration)
            }
        }

    else                          ->
        flow {
            repeat(count) {
                emitAll(flow)
                delay(durationFunc(it))
            }
        }
}
