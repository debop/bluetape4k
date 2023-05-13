package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class FlowFlatMapDrop<T, R>(
    private val source: Flow<T>,
    private val mapper: suspend (T) -> Flow<R>,
): AbstractFlow<R>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<R>) {
        coroutineScope {
            val resume = Resumable()
            val consumerReady = atomic(true)
            val value = atomic<T?>(null)
            val hasValue = atomic(false)
            val done = atomic(false)
            val error = atomic<Throwable?>(null)

            val job = launch {
                try {
                    source.collect { item ->
                        log.trace { "source collecting ... $item" }
                        if (consumerReady.value) {
                            consumerReady.value = false
                            value.lazySet(item)
                            hasValue.value = true
                            resume.resume()
                        }
                    }
                    done.value = true
                } catch (ex: Throwable) {
                    error.value = ex
                }
                resume.resume()
            }

            while (coroutineContext.isActive) {
                resume.await()

                error.value?.let { if (!hasValue.value) throw it }

                if (done.value && !hasValue.value) {
                    break
                }

                if (hasValue.value) {
                    val v = value.value!!
                    value.lazySet(null)
                    hasValue.value = false

                    try {
                        mapper(v)
                            .onEach { log.trace { "mapper collecting ... $it" } }
                            .collect { item -> collector.emit(item) }
                    } catch (e: Throwable) {
                        job.cancel()
                        throw e
                    }
                }

                consumerReady.value = true
            }
        }
    }
}
