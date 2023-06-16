package io.bluetape4k.coroutines.flow.extensions.group

import io.bluetape4k.collections.tryForEach
import io.bluetape4k.coroutines.flow.extensions.GroupedFlow
import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.support.uninitialized
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.cancellation.CancellationException

/**
 * Groups transformed values of the source flow based on a key selector function.
 */
@Deprecated("use groupByInternal")
@PublishedApi
internal class FlowGroupBy<T, K, V>(
    val source: Flow<T>,
    val keySelector: (T) -> K,
    val valueSelector: (T) -> V,
): AbstractFlow<GroupedFlow<K, V>>() {

    override suspend fun collectSafely(collector: FlowCollector<GroupedFlow<K, V>>) {
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
                            collector.emit(group)
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


    class FlowGroup<K, V>(
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
}
