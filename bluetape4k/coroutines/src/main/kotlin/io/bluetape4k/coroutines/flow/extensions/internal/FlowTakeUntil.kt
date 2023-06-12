package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * [notifier]의 요소가 존재하기 전까지만 [source]의 요소를 소비하고,
 * [notifier]에서 emit 이 되기 시작하면, [source]로부터 collect 를 중단합니다.
 *
 * @param T
 * @param U
 * @property source main source
 * @property notifier  notifier source
 */
internal class FlowTakeUntil<T, U>(
    private val source: Flow<T>,
    private val notifier: Flow<U>,
): AbstractFlow<T>() {

    companion object: KLogging() {
        private val STOP = StopException()
    }

    class StopException: CancellationException()

    override suspend fun collectSafely(collector: FlowCollector<T>) = coroutineScope {
        val gate = atomic(false)

        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                notifier.collect {
                    throw STOP
                }
            } catch (e: StopException) {
                // Nothing to do
            } finally {
                gate.value = true
            }
        }

        try {
            source.collect {
                if (gate.value) {
                    throw STOP
                }
                collector.emit(it)
            }
        } catch (e: StopException) {
            // Nothing to do
        } finally {
            job.cancel(STOP)
        }
    }
}
