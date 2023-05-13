package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch


internal class FlowOnBackpressureDrop<T>(private val source: Flow<T>): AbstractFlow<T>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        coroutineScope {
            val producerReady = Resumable()
            var consumerReady by atomic(false)
            val value = atomic<T>(uninitialized())
            var done by atomic(false)
            var error by atomic<Throwable?>(null)

            launch {
                try {
                    source.collect { item ->
                        if (consumerReady) {
                            value.lazySet(item)
                            consumerReady = false
                            producerReady.resume()
                        }
                    }
                    done = true
                } catch (e: Throwable) {
                    error = e
                }
                producerReady.resume()
            }

            while (true) {
                consumerReady = true
                producerReady.await()

                error?.let { throw it }

                if (done) {
                    break
                }

                collector.emit(value.getAndSet(uninitialized()))
            }
        }
    }
}
