package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Merges an array of Flow instances in an unbounded manner.
 */
class FlowMergeArray<T>(private val sources: List<Flow<T>>): AbstractFlow<T>() {

    companion object: KLogging()

    constructor(vararg sources: Flow<T>): this(sources.toList())

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val queue = ConcurrentLinkedQueue<T>()
        val done = atomic(sources.size)
        val ready = Resumable()

        coroutineScope {
            // 모든 source 로부터 요소를 받아 queue 에 저장한다.
            sources.forEach { source ->
                launch {
                    try {
                        source.collect {
                            queue.offer(it)
                            ready.resume()
                        }
                    } finally {
                        done.decrementAndGet()
                        ready.resume()
                    }
                }
            }

            while (true) {
                val isDone = done.value == 0
                val value = queue.poll()

                when {
                    isDone && value == null -> break
                    value != null -> collector.emit(value)
                    else -> ready.await()
                }
            }
        }
    }
}
