package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.AtomicIntArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

@Deprecated("use concatArrayEagerInternal")
class FlowConcatArrayEager<T>(private val sources: List<Flow<T>>): AbstractFlow<T>() {

    companion object: KLogging()

    constructor(vararg sources: Flow<T>): this(sources.toList())

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        coroutineScope {
            val size = sources.size
            val queues = List(size) { ConcurrentLinkedQueue<T>() }
            val dones = AtomicIntArray(sources.size)
            val reader = Resumable()

            repeat(size) {
                val f = sources[it]
                val q = queues[it]
                launch {
                    try {
                        f.collect { item ->
                            log.trace { "collect from source[$it] item=$item" }
                            q.offer(item)
                            reader.resume()
                        }
                    } finally {
                        dones[it].value = 1
                        reader.resume()
                    }
                }
            }

            var index = 0
            while (isActive && index < size) {
                val queue = queues[index]
                val done = dones[index].value != 0

                if (done && queue.isEmpty()) {
                    index++
                    continue
                }
                val value = queue.poll()
                if (value != null) {
                    collector.emit(value)
                    continue
                }
                reader.await()
            }
        }
    }
}
