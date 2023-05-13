package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the array of [Flow]s
 */
internal class FlowAmbIterable<T>(private val sources: Iterable<Flow<T>>): Flow<T> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T> invoke(vararg sources: Flow<T>): FlowAmbIterable<T> {
            return FlowAmbIterable(sources.asIterable())
        }

        @JvmStatic
        operator fun <T> invoke(sources: Collection<Flow<T>>): FlowAmbIterable<T> {
            return FlowAmbIterable(sources)
        }
    }

    override suspend fun collect(collector: FlowCollector<T>) = coroutineScope {
        val winner = atomic(0)
        val jobs = ConcurrentHashMap<Job, Int>()

        var i = 1
        sources.forEach { source ->
            val idx = i
            val job = launch {
                source.collect {
                    if (winner.value == idx) {
                        collector.emit(it)
                    } else if (winner.value == 0 && winner.compareAndSet(0, idx)) {
                        jobs.forEach { (job, j) ->
                            if (j != idx) {
                                job.cancel()
                            }
                        }
                        collector.emit(it)
                    } else {
                        throw CancellationException()
                    }
                }
            }

            jobs[job] = i
            if (winner.value != 0 && winner.value != i) {
                jobs.remove(job)
                job.cancel()
            } else {
                i++
            }
        }
    }
}
