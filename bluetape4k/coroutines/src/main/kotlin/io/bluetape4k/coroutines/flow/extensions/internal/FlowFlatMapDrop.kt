package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Deprecated("use flatMapFirst")
class FlowFlatMapDrop<T, R>(
    private val source: Flow<T>,
    private val mapper: suspend (value: T) -> Flow<R>,
): AbstractFlow<R>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<R>) {
        coroutineScope {
            val resume = Resumable()
            var consumerReady by atomic(true)
            var value by atomic<T?>(null)
            var hasValue by atomic(false)
            var done by atomic(false)
            var error by atomic<Throwable?>(null)

            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    source.collect { item ->
                        log.trace { "source collecting ... $item" }
                        if (consumerReady) {
                            consumerReady = false
                            value = item
                            hasValue = true
                            resume.resume()
                        }
                    }
                    done = true
                } catch (ex: Throwable) {
                    error = ex
                }
                resume.resume()
            }

            while (coroutineContext.isActive) {
                resume.await()

                error?.let { if (!hasValue) throw it }

                if (done && !hasValue) {
                    break
                }

                if (hasValue) {
                    val v = value!!
                    value = null
                    hasValue = false

                    try {
                        mapper(v)
                            .onEach { log.trace { "mapper collecting ... $it" } }
                            .collect { item -> collector.emit(item) }
                    } catch (e: Throwable) {
                        job.cancel()
                        throw e
                    }
                }

                consumerReady = true
            }
        }
    }
}
