package io.bluetape4k.coroutines.flow.extensions.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

/**
 * [notifier]의 요소가 존재하기 전까지만 [source]의 요소를 skip하고,
 * [notifier]이 complete 되면, [source]로부터 collect 를 시작합니다.
 *
 * @param T
 * @param U
 * @property source main source
 * @property notifier  notifier source
 */
internal class FlowSkipUntil<T, U>(
    private val source: Flow<T>,
    private val notifier: Flow<U>,
): AbstractFlow<T>() {

    override suspend fun collectSafely(collector: FlowCollector<T>) = coroutineScope {
        val gate = atomic(false)

        val job = launch {
            try {
                notifier.take(1).collect()
            } catch (e: CancellationException) {
                // Nothing to do 
            } finally {
                gate.value = true
            }
        }

        try {
            source.collect {
                if (gate.value) {
                    collector.emit(it)
                }
            }
        } catch (e: CancellationException) {
            // Nothing to do
        } finally {
            job.cancel()
        }
    }
}
