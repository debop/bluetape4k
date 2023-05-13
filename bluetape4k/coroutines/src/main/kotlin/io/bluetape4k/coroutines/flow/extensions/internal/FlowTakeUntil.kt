package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * [other]의 요소가 존재하기 전까지만 [source]의 요소를 소비하고,
 * [other]에서 emit 이 되기 시작하면, [source]로부터 collect 를 중단합니다.
 *
 * @param T
 * @param U
 * @property source main source
 * @property other  second source
 */
internal class FlowTakeUntil<T, U>(
    private val source: Flow<T>,
    private val other: Flow<U>,
): AbstractFlow<T>() {

    companion object: KLogging() {
        val STOP = StopException()
    }

    class StopException: CancellationException()

    override suspend fun collectSafely(collector: FlowCollector<T>) = coroutineScope {
        var gate by atomic(false)

        val job = launch {
            try {
                other.collect {
                    throw STOP
                }
            } catch (e: StopException) {
                // Nothing to do
            } finally {
                gate = true
            }
        }

        try {
            source.collect {
                if (gate) {
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
