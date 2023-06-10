package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowTakeUntil
import kotlinx.coroutines.flow.Flow
import java.time.Duration

/**
 * [other] 소스에서 요소가 emit 되거나 완료될 때까지는 main source로부터 소비한다.
 */
fun <T, U> Flow<T>.takeUntil(other: Flow<U>): Flow<T> =
    FlowTakeUntil(this, other)

/**
 * [delay] 만큼 지연해서 flow 를 collect 하도록 합니다.
 *
 * @param T
 * @param delay 지연할 시간
 * @return
 */
fun <T> Flow<T>.takeUntil(delay: Duration): Flow<T> =
    FlowTakeUntil(this, delayedFlow(delay))

/**
 * [delayMillis] 만큼 지연해서 flow 를 collect 하도록 합니다.
 *
 * @param T
 * @param delayMillis 지연할 시간 (단위: MilliSeconds)
 * @return
 */
fun <T> Flow<T>.takeUntil(delayMillis: Long): Flow<T> =
    FlowTakeUntil(this, delayedFlow(delayMillis))
