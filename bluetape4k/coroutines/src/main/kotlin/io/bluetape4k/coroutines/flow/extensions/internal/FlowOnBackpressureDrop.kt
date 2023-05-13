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
            val consumerReady = atomic(false)
            val producerReady = Resumable()
            val value = atomic<T>(uninitialized())
            val done = atomic(false)
            val error = atomic<Throwable?>(null)

            launch {
                try {
                    source.collect { item ->
                        if (consumerReady.value) {
                            value.value = item
                            consumerReady.value = false
                            producerReady.resume()
                        }
                    }
                    done.value = true
                } catch (e: Throwable) {
                    error.value = e
                }
                producerReady.resume()
            }

            while (true) {
                consumerReady.value = true
                producerReady.await()

                error.value?.let { throw it }

                if (done.value) {
                    break
                }

                collector.emit(value.getAndSet(uninitialized()))
            }
        }
    }
}
