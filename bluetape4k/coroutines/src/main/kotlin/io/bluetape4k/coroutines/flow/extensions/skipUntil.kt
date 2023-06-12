@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowSkipUntil
import kotlinx.coroutines.flow.Flow
import java.time.Duration

/**
 * 두 번째 [Flow]([notifier])이 값을 방출하거나 완료될 때까지 소스 [Flow]에서 방출된 항목을 건너뛰는 [Flow]을 반환합니다.
 *
 * @param notifier 소스 [Flow]의 흐름을 제어하는 [Flow]
 */
fun <T> Flow<T>.skipUntil(notifier: Flow<Any?>): Flow<T> = FlowSkipUntil(this, notifier)

/**
 * [delay] 동안 소스 [Flow]에서 방출된 항목을 건너뛰는 [Flow]를 반환합니다.
 *
 * @param delay 건너뛰기 할 시간
 */
fun <T> Flow<T>.skipUntil(delay: Duration): Flow<T> = skipUntil(delayedFlow(delay))

/**
 * [delayMillis] 동안 소스 [Flow]에서 방출된 항목을 건너뛰는 [Flow]를 반환합니다.
 *
 * @param delayMillis 건너뛰기 할 시간 (millis seconds)
 */
fun <T> Flow<T>.skipUntil(delayMillis: Long): Flow<T> = skipUntil(delayedFlow(delayMillis))

/**
 * [skipUnitil] 과 같은 기능을 하는 operator
 *
 * @param notifier 소스 [Flow]의 흐름을 제어하는 [Flow]
 */
fun <T> Flow<T>.dropUntil(notifier: Flow<Any?>): Flow<T> = skipUntil(notifier)

/**
 * [delay] 동안 소스 [Flow]에서 방출된 항목을 건너뛰는 [Flow]를 반환합니다.
 *
 * @param delay 건너뛰기 할 시간
 */
fun <T> Flow<T>.dropUntil(delay: Duration): Flow<T> = skipUntil(delayedFlow(delay))

/**
 * [delayMillis] 동안 소스 [Flow]에서 방출된 항목을 건너뛰는 [Flow]를 반환합니다.
 *
 * @param delayMillis 건너뛰기 할 시간 (millis seconds)
 */
fun <T> Flow<T>.dropUntil(delayMillis: Long): Flow<T> = skipUntil(delayedFlow(delayMillis))
