@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.support.uninitialized
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Drops items from the upstream when the downstream is not ready to receive them.
 */
fun <T> Flow<T>.onBackpressureDrop(): Flow<T> = onBackpressureDropInternal(this)
//FlowOnBackpressureDrop(this)

internal fun <T> onBackpressureDropInternal(source: Flow<T>): Flow<T> = flow {
    coroutineScope {
        val producerReady = Resumable()
        val consumerReady = atomic(false)
        val value = atomic<T>(uninitialized())
        val done = atomic(false)
        val error = atomic<Throwable?>(null)

        launch {
            try {
                source.collect { item ->
                    if (consumerReady.value) {
                        value.lazySet(item)
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

            emit(value.getAndSet(uninitialized()))
        }
    }
}
