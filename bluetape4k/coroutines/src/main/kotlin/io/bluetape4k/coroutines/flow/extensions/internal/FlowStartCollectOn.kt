package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.ResumableCollector
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@Deprecated("use startCollectOnInternal")
internal class FlowStartCollectOn<T>(
    val source: Flow<T>,
    private val coroutineDispatcher: CoroutineContext,
): AbstractFlow<T>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<T>) = coroutineScope {
        val inner = ResumableCollector<T>()

        launch(coroutineDispatcher) {
            try {
                source.collect {
                    inner.next(it)
                }
                inner.complete()
            } catch (e: Throwable) {
                inner.error(e)
            }
        }

        inner.drain(collector)
    }
}
