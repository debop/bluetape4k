@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

/**
 * 초기 지연시간[initialDelay] 이후에 0을 emit 하는 [Flow] 를 생성합니다.
 *
 * @param initialDelay 첫번째 요소를 보내기 전의 초기 지연 시간
 */
fun flowOfDelay(initialDelay: Duration): Flow<Long> = delayedFlow(initialDelay)

fun flowOfDelay(initialDelayMillis: Long): Flow<Long> = delayedFlow(initialDelayMillis)

fun flowWithDelay(initialDelay: Duration): Flow<Long> = delayedFlow(initialDelay)

fun flowWithDelay(initialDelayMillis: Long): Flow<Long> = delayedFlow(initialDelayMillis)

/**
 * 지연 시간[delay] 이후에 0을 발행하는 [Flow] 를 생성합니다.
 *
 * @param delay 지연 시간
 * @return 지연 시간 이후 0을 발행하는 [Flow]
 */
fun delayedFlow(delay: Duration): Flow<Long> = delayedFlow(delay.inWholeMilliseconds)

fun delayedFlow(delayMillis: Long): Flow<Long> = flow {
    delay(delayMillis.coerceAtLeast(0L))
    emit(0L)
}

/**
 * 지연 시간[initialDelay] 후 [value] 를 emit 하는 [Flow] 를 생성합니다.
 */
fun <T> flowWithDelay(value: T, initialDelay: Duration): Flow<T> = delayedFlow(value, initialDelay)

/**
 * 지연 시간[initialDelayMillis] 후 [value] 를 emit 하는 [Flow] 를 생성합니다.
 */
fun <T> flowWithDelay(value: T, initialDelayMillis: Long): Flow<T> = delayedFlow(value, initialDelayMillis)

/**
 * 지연 시간[delay] 후 [value]를 발행하는 [Flow] 를 생성합니다.
 *
 * ```
 * val flow = delayedFlow(1, 2.seconds)
 * ```
 */
fun <T> delayedFlow(value: T, duration: Duration): Flow<T> =
    delayedFlow(value, duration.inWholeMilliseconds)

/**
 * 지연 시간[delay] 후 [value]를 발행하는 [Flow] 를 생성합니다.
 *
 * ```
 * val flow = delayedFlow(1, 2_000L)
 * ```
 */
fun <T> delayedFlow(value: T, delayMillis: Long): Flow<T> = flow {
    delay(delayMillis.coerceAtLeast(0))
    emit(value)
}
