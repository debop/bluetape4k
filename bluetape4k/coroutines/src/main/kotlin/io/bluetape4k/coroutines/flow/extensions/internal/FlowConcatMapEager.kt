package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.Resumable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

@Deprecated("use concatMapEagerInternal")
class FlowConcatMapEager<T, R>(
    private val source: Flow<T>,
    private val mapper: suspend (t: T) -> Flow<R>,
): AbstractFlow<R>() {

    companion object: KLogging()

    override suspend fun collectSafely(collector: FlowCollector<R>) {
        coroutineScope {
            val resumeOutput = Resumable()
            val innerQueues = ConcurrentLinkedQueue<InnerQueue<R>>()
            val innerDone = atomic(false)

            launch {
                try {
                    source.collect { item ->
                        log.trace { "source item=$item" }
                        val f = mapper(item)
                        val newQueue = InnerQueue<R>()
                        innerQueues.offer(newQueue)
                        resumeOutput.resume()
                        launch {
                            try {
                                f.collect {
                                    log.trace { "mapped item=$it" }
                                    newQueue.queue.offer(it)
                                    resumeOutput.resume()
                                }
                            } finally {
                                newQueue.done.value = true
                                resumeOutput.resume()
                            }
                        }
                    }
                } finally {
                    innerDone.value = true
                    resumeOutput.resume()
                }
            }

            var innerQueue: InnerQueue<R>? = null
            while (isActive) {
                if (innerQueue == null) {
                    val done = innerDone.value
                    innerQueue = innerQueues.poll()

                    if (done && innerQueue == null) {
                        break
                    }
                }
                if (innerQueue != null) {
                    val done = innerQueue.done.value
                    val value = innerQueue.queue.poll()

                    if (done && value == null) {
                        innerQueue = null
                        continue
                    }
                    if (value != null) {
                        collector.emit(value)
                        continue
                    }
                }
                // 다음 item이 올때까지 대기한다
                resumeOutput.await()
            }
        }
    }

    class InnerQueue<R> {
        val queue = ConcurrentLinkedQueue<R>()
        val done = atomic(false)
    }
}
