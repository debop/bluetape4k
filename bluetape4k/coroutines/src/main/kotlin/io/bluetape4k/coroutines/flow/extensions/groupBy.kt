@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.collections.tryForEach
import io.bluetape4k.support.uninitialized
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.cancellation.CancellationException

/**
 * Key 로 Grouping 된 [Flow] 를 표현하는 interface 입니다.
 */
interface GroupedFlow<K, V>: Flow<V> {

    /**
     * The grouped key of the flow
     */
    val key: K
}

/**
 * [GroupedFlow]의 Value들만 묶어, `List<V>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.toValues(): Flow<List<V>> = flowFromSuspend { toList() }

data class GroupItem<K, V>(
    val key: K,
    val values: List<V>,
): Serializable

/**
 * [GroupedFlow]의 [key] 와 Value들을 묶어, `Pair<K, List<V>>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.toGroupItems(): Flow<GroupItem<K, V>> = flowFromSuspend {
    val values = toList()
    GroupItem(key, values)
}

suspend fun <K, V> Flow<GroupedFlow<K, V>>.toMap(destination: MutableMap<K, List<V>> = mutableMapOf()): MutableMap<K, List<V>> {
    this.flatMapMerge { it.toGroupItems() }
        .collect { groupItem ->
            destination[groupItem.key] = groupItem.values
        }
    return destination
}

fun <T, K> Flow<T>.groupBy(keySelector: (T) -> K): Flow<GroupedFlow<K, T>> =
    groupByInternal(this, keySelector) { it }
// FlowGroupBy(this, keySelector) { it }

fun <T, K, V> Flow<T>.groupBy(keySelector: (T) -> K, valueSelector: (T) -> V): Flow<GroupedFlow<K, V>> =
    groupByInternal(this, keySelector, valueSelector)
// FlowGroupBy(this, keySelector, valueSelector)


/**
 * Groups transformed values of the source flow based on a key selector function.
 */
@PublishedApi
internal fun <T, K, V> groupByInternal(
    source: Flow<T>,
    keySelector: (T) -> K,
    valueSelector: (T) -> V,
): Flow<GroupedFlow<K, V>> = flow {
    val map = ConcurrentHashMap<K, FlowGroup<K, V>>()
    val mainStopped = atomic(false)

    try {
        source.collect {
            val k = keySelector(it)
            var group = map[k]
            if (group != null) {
                group.next(valueSelector(it))
            } else {
                if (!mainStopped.value) {
                    group = FlowGroup(k, map)
                    map[k] = group

                    try {
                        emit(group)
                    } catch (e: CancellationException) {
                        mainStopped.value = true
                        if (map.size == 0) {
                            throw CancellationException()
                        }
                    }
                    group.next(valueSelector(it))
                } else {
                    if (map.size == 0) {
                        throw CancellationException()
                    }
                }
            }
        }
        map.values.tryForEach { it.complete() }
    } catch (e: Throwable) {
        map.values.tryForEach { it.error(e) }
        throw e
    }
}

private class FlowGroup<K, V>(
    override val key: K,
    private val map: ConcurrentMap<K, FlowGroup<K, V>>,
): AbstractFlow<V>(), GroupedFlow<K, V> {

    @Volatile
    private var value: V = uninitialized()
    private var error: Throwable? = null

    private val hasValue = atomic(false)
    private val done = atomic(false)
    private val cancelled = atomic(false)

    private val consumerReady = Resumable()
    private val valueReady = Resumable()

    private var once = atomic(false)

    override suspend fun collectSafely(collector: FlowCollector<V>) {
        if (!once.compareAndSet(expect = false, update = true)) {
            error("A GroupedFlow can only be collected at most once.")
        }

        consumerReady.resume()

        while (true) {
            val d = done.value
            val has = hasValue.value

            if (d && !has) {
                error?.let { throw it }
                break
            }

            if (has) {
                val v = value
                value = uninitialized()
                hasValue.value = false

                try {
                    collector.emit(v)
                } catch (e: Throwable) {
                    map.remove(this.key)
                    cancelled.value = true
                    consumerReady.resume()
                    throw e
                }

                consumerReady.resume()
                continue
            }

            valueReady.await()
        }
    }

    suspend fun next(value: V) {
        if (cancelled.value) return

        consumerReady.await()
        this.value = value
        this.hasValue.value = true
        valueReady.resume()
    }

    fun error(ex: Throwable) {
        error = ex
        done.value = true
        valueReady.resume()
    }

    fun complete() {
        done.value = true
        valueReady.resume()
    }
}
