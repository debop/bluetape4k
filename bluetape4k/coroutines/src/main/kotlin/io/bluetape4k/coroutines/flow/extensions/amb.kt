@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.yield

/**
 * 모든 소스 [Flow]들을 수집하고 첫 번째 요소를 발행하는 Flow를 시작하며, 나머지는 취소합니다.
 *
 * ```
 * val flow1 = flowRangeOf(1, 5).onStart { delay(1000) }
 * val flow2 = flowRangeOf(6, 5).onStart { delay(100) }
 *
 * amb(flow1, flow2)  // 6, 7, 8, 9, 10
 * ```
 */
fun <T> amb(flow1: Flow<T>, flow2: Flow<T>, vararg flows: Flow<T>): Flow<T> =
    ambInternal(
        buildList(capacity = 2 + flows.size) {
            add(flow1)
            add(flow2)
            addAll(flows)
        }
    )

/**
 * 모든 소스 [Flow]들을 수집하고 첫 번째 요소를 발행하는 Flow를 시작하며, 나머지는 취소합니다.
 *
 * ```
 * val flow1 = flowRangeOf(1, 5).onStart { delay(1000) }
 * val flow2 = flowRangeOf(6, 5).onStart { delay(100) }
 *
 * listOf(flow1, flow2).amb()   // 6, 7, 8, 9, 10
 * ```
 *
 * @receiver the [Iterable] sequence of [Flow]s
 */
fun <T> Iterable<Flow<T>>.amb(): Flow<T> = ambInternal(this)

/**
 * 모든 소스 [Flow]들을 수집하고 첫 번째 요소를 발행하는 Flow를 시작하며, 나머지는 취소합니다.
 *
 * ```
 * val flow1 = flowRangeOf(1, 5).onStart { delay(1000) }
 * val flow2 = flowRangeOf(6, 5).onStart { delay(100) }
 *
 * flow1.ambWith(flow2)  // 6, 7, 8, 9, 10
 * ```
 */
fun <T> Flow<T>.ambWith(flow1: Flow<T>, vararg flows: Flow<T>): Flow<T> = amb(this, flow1, *flows)

internal fun <T> ambInternal(sources: Iterable<Flow<T>>): Flow<T> = flow {
    coroutineScope {
        val channels = sources.map { flow ->
            // Produce the values using the default (rendezvous) channel
            produce {
                flow.collect {
                    send(it)
                    yield() // Emulate fairness, giving each flow chance to emit
                }
            }
        }

        if (channels.isEmpty()) {
            return@coroutineScope
        }

        channels
            .singleOrNull()
            ?.let { return@coroutineScope emitAll(it) }

        val (winnerIndex, winnerResult) = select {
            channels.forEachIndexed { index, channel ->
                channel.onReceiveCatching {
                    index to it
                }
            }
        }

        channels.forEachIndexed { index, channel ->
            if (index != winnerIndex) {
                channel.cancel()
            }
        }

        winnerResult
            .onSuccess {
                emit(it)
                emitAll(channels[winnerIndex])
            }
            .onFailure {
                it?.let { throw it }
            }
    }
}
