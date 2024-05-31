@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.AtomicIntArray
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

private val log by lazy { KotlinLogging.logger { } }

/**
 * 모든 [Flow]를 동시에 시작하고, 다음 소스의 항목이 발행되기 전에 소스에서 모든 항목을 발행합니다.
 * 각 소스는 무제한으로 소비되므로 현재 소스와 수집기의 속도에 따라 연산자가 항목을 더 오래 유지하고 실행 중에 더 많은 메모리를 사용할 수 있습니다.
 *
 * ```
 * listOf(
 *     flowRangeOf(1, 5).onStart { delay(100) },
 *     flowRangeOf(6, 5),
 * )
 *     .concatFlows()
 *     .take(6)    // 1, 2, 3, 4, 5, 6
 * ```
 *
 * @param T
 * @return
 */
fun <T> Iterable<Flow<T>>.concatFlows(): Flow<T> =
    concatArrayEagerInternal(this.toList())

/**
 * 모든 [sources]를 동시에 시작하고, 다음 소스의 항목이 발행되기 전에 소스에서 모든 항목을 발행합니다.
 * 각 소스는 무제한으로 소비되므로 현재 소스와 수집기의 속도에 따라 연산자가 항목을 더 오래 유지하고 실행 중에 더 많은 메모리를 사용할 수 있습니다.
 *
 * ```
 * flowOf(
 *     flowRangeOf(1, 5).onStart { delay(100) },
 *     flowRangeOf(6, 5),
 * )
 *     .concatFlows()
 *     .take(6)    // 1, 2, 3, 4, 5, 6
 * ```
 */
suspend fun <T> Flow<Flow<T>>.concatFlows(): Flow<T> =
    concatArrayEagerInternal(this.toList())

/**
 * 모든 [sources]를 동시에 시작하고, 다음 소스의 항목이 발행되기 전에 소스에서 모든 항목을 발행합니다.
 * 각 소스는 무제한으로 소비되므로 현재 소스와 수집기의 속도에 따라 연산자가 항목을 더 오래 유지하고 실행 중에 더 많은 메모리를 사용할 수 있습니다.
 *
 * ```
 * val flow1 = flowRangeOf(1, 5)
 *     .onStart {
 *         delay(200)
 *         state1.value = 1
 *     }
 * val flow2 = flowRangeOf(6, 5)
 *     .onStart { state2.value = state1.value }
 *
 * concatArrayEager(flow1, flow2)   // (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 * ```
 */
fun <T> concatArrayEager(vararg sources: Flow<T>): Flow<T> = // FlowConcatArrayEager(*sources)
    concatArrayEagerInternal(sources.asList())

/**
 * 모든 [sources]를 동시에 시작하고, 다음 소스의 항목이 발행되기 전에 소스에서 모든 항목을 발행합니다.
 * 각 소스는 무제한으로 소비되므로 현재 소스와 수집기의 속도에 따라 연산자가 항목을 더 오래 유지하고 실행 중에 더 많은 메모리를 사용할 수 있습니다.
 *
 *
 * @param T
 * @param sources collect 할 모든 [Flow]들
 * @return collect 한 모든 [Flow]들을 순서대로 발행하는 [Flow]
 */
internal fun <T> concatArrayEagerInternal(sources: List<Flow<T>>): Flow<T> = channelFlow {
    coroutineScope {
        val size = sources.size
        val queues = List(size) { ConcurrentLinkedQueue<T>() }
        val dones = AtomicIntArray(sources.size)
        val reader = Resumable()

        repeat(size) {
            val f = sources[it]
            val q = queues[it]
            launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    f.collect { item ->
                        log.trace { "collect from source[$it] item=$item" }
                        q.offer(item)
                        reader.resume()
                    }
                } finally {
                    dones[it].value = 1
                    reader.resume()
                }
            }
        }

        var index = 0
        while (isActive && index < size) {
            val queue = queues[index]
            val done = dones[index].value != 0

            if (done && queue.isEmpty()) {
                index++
                continue
            }
            val value = queue.poll()
            if (value != null) {
                send(value)
                continue
            }
            reader.await()
        }
    }
}
