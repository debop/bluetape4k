package io.bluetape4k.coroutines

import java.util.concurrent.locks.ReentrantLock
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
import kotlin.concurrent.withLock
import kotlin.coroutines.CoroutineContext


/**
 * CoroutineScope 하에서 지연된 값을 계산하는 suspend 함수를 이용하여 값을 계산합니다.
 *
 * @param T
 * @property dispatcher
 * @property block
 */
internal class LazyDeferred<T>(
    val coroutineContext: CoroutineContext = Dispatchers.Default,
    private val block: suspend CoroutineScope.() -> T,
) {

    private val lock = ReentrantLock()

    @Volatile
    private var deferred: Deferred<T>? = null

    internal fun start(scope: CoroutineScope) {
        if (deferred == null) {
            lock.withLock {
                if (deferred == null) {
                    deferred = scope.async(coroutineContext, block = block)
                }
            }
        }
    }

    suspend fun await(): T = deferred?.await() ?: error("Coroutine not started")
}


/**
 * [Flow] 를 [AsyncFlow] 로 변환하여, 각 요소처리를 비동기 방식으로 수행하게 합니다.
 * 단 `flatMapMerge` 처럼 실행완료된 순서로 반환하는 것이 아니라, Flow 의 처음 요소의 순서대로 반환합니다. (Deferred 형식으로)
 */
fun <T, R> Flow<T>.asyncFlow(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.(T) -> R,
): AsyncFlow<R> {
    val deferredFlow = map { LazyDeferred(coroutineContext) { block(it) } }
    return AsyncFlow(deferredFlow)
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
class AsyncFlow<T> internal constructor(internal val deferredFlow: Flow<LazyDeferred<T>>) {

    /**
     * [Flow]와 마찮가지로 emit된 요소를 collect 합니다.
     *
     * @param collector emit 된 요소를 collect 하는 [FlowCollector]
     */
    suspend fun collect(collector: FlowCollector<T>) {
        deferredFlow.collect {
            collector.emit(it.await())
        }
    }
}

/**
 * [AsyncFlow] 의 요소들을 비동기로 실행하고, 순차적으로 수집합니다.
 */
suspend fun <T> AsyncFlow<T>.asyncCollect(capacity: Int = Channel.BUFFERED, collector: FlowCollector<T>) {
    channelFlow {
        deferredFlow.buffer(capacity)
            .collect {
                it.start(this)
                send(it)
            }
    }.collect {
        collector.emit(it.await())
    }
}

/**
 * [AsyncFlow] 의 요소들을 [action]을 통해 비동기로 실행하고, 수집합니다.
 */
suspend inline fun <T> AsyncFlow<T>.asyncCollect(
    capacity: Int = Channel.BUFFERED,
    crossinline action: suspend (value: T) -> Unit,
) {
    asyncCollect(
        capacity,
        FlowCollector { value -> action(value) }
    )
}

/**
 * [AsyncFlow] 의 요소들을 비동기로 매핑합니다.
 */
fun <T, R> AsyncFlow<T>.asyncMap(mapper: suspend (T) -> R): AsyncFlow<R> =
    AsyncFlow(deferredFlow.map { input ->
        LazyDeferred(input.coroutineContext) {
            input.start(this)
            mapper(input.await())
        }
    })


/**
 * Flow 를 [AsyncFlow] 로 변환합니다.
 *
 * ```
 * (1..100)
 *     .asFlow()
 *     .asAsyncFlow() {
 *         it * it
 *     }
 *     .asyncCollect {
 *         // something to do
 *     }
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T, R> Flow<T>.asAsyncFlow(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend (T) -> R = { it as R },
): AsyncFlow<R> {
    val deferredFlow = map { input -> LazyDeferred(coroutineContext) { block(input) } }
    return AsyncFlow(deferredFlow)
}
