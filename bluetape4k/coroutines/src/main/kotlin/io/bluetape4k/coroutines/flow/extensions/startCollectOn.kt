@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Stats collecting the upstream on the specified dispatcher.
 */
fun <T> Flow<T>.startCollectOn(dispatcher: CoroutineDispatcher): Flow<T> =
    startCollectOnInternal(this, dispatcher)
// FlowStartCollectOn(this, dispatcher)

internal fun <T> startCollectOnInternal(
    source: Flow<T>,
    dispatcher: CoroutineDispatcher,
): Flow<T> = flow {

    coroutineScope {
        val inner = ResumableCollector<T>()

        launch(dispatcher) {
            try {
                source.collect {
                    inner.next(it)
                }
                inner.complete()
            } catch (e: Throwable) {
                inner.error(e)
            }
        }

        inner.drain(this@flow)
    }
}
