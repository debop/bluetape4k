@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.exception.StopException
import io.bluetape4k.coroutines.flow.exception.checkOwnership
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

/**
 * Represents all of the notifications from the source [Flow] as `value`
 * emissions marked with their original types within [FlowEvent] objects.
 *
 * Flow 흐름을 Reactive Signal 형태인 [FlowEvent] 로 변환하여 제공합니다.
 * 이렇게 하면 Reactor `Flux` 와 같은 Reactive Stream 을 사용하는 라이브러리와의 연동이 쉬워집니다.
 *
 * @param T
 * @return
 */
fun <T> Flow<T>.materialize(): Flow<FlowEvent<T>> =
    map<T, FlowEvent<T>> { FlowEvent.Value(it) }
        .onCompletion { if (it == null) emit(FlowEvent.Complete) }
        .catch { ex -> emit(FlowEvent.Error(ex)) }

/**
 * Converts a [Flow] of [FlowEvent] objects (materialized flow) into the emissions that they represent.
 * [materialize]된 Flow를 다시 원래의 Flow로 변환합니다. emit 되는 [FlowEvent]를 다시 원래의 값으로 변환합니다.
 */
fun <T> Flow<FlowEvent<T>>.dematerialize(): Flow<T> = flow {
    try {
        collect {
            when (it) {
                is FlowEvent.Value -> emit(it.value)
                is FlowEvent.Error -> throw it.error
                FlowEvent.Complete -> throw StopException(this)  // 완료 시 [StopException] 을 발생시켜 Flow 를 중단합니다.
            }
        }
    } catch (e: StopException) {
        e.checkOwnership(this@flow)
    }
}
