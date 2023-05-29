package io.bluetape4k.coroutines.flow

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext


/**
 * suspend 함수를 비동기 실행으로 값을 계산합니다.
 *
 * @param T
 * @property coroutineContext
 * @property block
 */
@PublishedApi
internal class LazyDeferred<out T>(
    val coroutineContext: CoroutineContext = Dispatchers.Default,
    val block: suspend CoroutineScope.() -> T,
) {
    private val deferred = atomic<Deferred<T>?>(null)
    // private val lock = ReentrantLock()

    fun start(scope: CoroutineScope) {
        deferred.compareAndSet(null, scope.async(coroutineContext, block = block))
    }

    suspend fun await(): T = deferred.value?.await() ?: error("Coroutine not started")
}

/**
 * Flow 형식을 취하지만 요소가 [LazyDeferred] 형식을 가지며, 실제 값을 계산하기 위해서는 await로 대기하게 됩니다.
 *
 * ```
 * (0..10).asFlow()
 *     .asyncFlow {
 *          delay(100)
 *          it * 2
 *     }
 *     .asyncCollect {
 *         println(it)
 *     }
 * ```
 *
 * @param T 요소의 수형
 * @property deferredFlow [LazyDeferred]를 emit 하는 [Flow] 인스턴스
 */
class AsyncFlow<out T> @PublishedApi internal constructor(
    @PublishedApi internal val deferredFlow: Flow<LazyDeferred<T>>,
): Flow<T> {

    /**
     * [Flow]와 마찮가지로 emit된 요소를 collect 합니다.
     *
     * @param collector emit 된 요소를 collect 하는 [FlowCollector]
     */
    override suspend fun collect(collector: FlowCollector<T>) {
        channelFlow {
            deferredFlow.collect { defer ->
                defer.start(this)
                send(defer)
            }
        }.collect { defer ->
            collector.emit(defer.await())
        }
    }
}

/**
 * [Flow] 를 [AsyncFlow] 로 변환하여, 각 요소처리를 비동기 방식으로 수행하게 합니다.
 * 단 `flatMapMerge` 처럼 실행완료된 순서로 반환하는 것이 아니라, Flow 의 처음 요소의 순서대로 반환합니다. (Deferred 형식으로)
 */
inline fun <T, R> Flow<T>.async(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline block: suspend CoroutineScope.(T) -> R,
): AsyncFlow<R> {
    val deferredFlow = map { input -> LazyDeferred(coroutineContext) { block(input) } }
    return AsyncFlow(deferredFlow)
}

/**
 * [AsyncFlow] 의 요소들을 비동기로 매핑합니다.
 */
inline fun <T, R> AsyncFlow<T>.map(crossinline transform: suspend (value: T) -> R): AsyncFlow<R> =
    AsyncFlow(deferredFlow.map { input ->
        LazyDeferred(input.coroutineContext) {
            input.start(this)
            transform(input.await())
        }
    })

/**
 * [AsyncFlow] 의 요소들을 비동기로 실행하고, 순차적으로 수집합니다.
 */
suspend fun <T> AsyncFlow<T>.collect(capacity: Int = Channel.BUFFERED, collector: FlowCollector<T> = NoopCollector) {
    channelFlow {
        deferredFlow
            .buffer(capacity)
            .collect { defer ->
                defer.start(this)
                send(defer)
            }
    }.collect { defer ->
        collector.emit(defer.await())
    }
}

/**
 * [AsyncFlow] 의 요소들을 [collector]을 통해 비동기로 실행하고, 수집합니다.
 */
suspend inline fun <T> AsyncFlow<T>.collect(
    capacity: Int = Channel.BUFFERED,
    crossinline collector: suspend (value: T) -> Unit,
) {
    collect(capacity, FlowCollector { value -> collector(value) })
}
