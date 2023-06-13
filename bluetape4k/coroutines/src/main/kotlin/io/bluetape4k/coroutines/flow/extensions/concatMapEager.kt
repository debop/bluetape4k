@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

private val log = KotlinLogging.logger { }

/**
 * Maps the upstream values into [Flow]s and launches them all at once, then
 * emits items from a source before items of the next are emitted.
 * Note that the upstream and each source is consumed in an unbounded manner and thus,
 * depending on the speed of the current source and the collector, the operator may retain
 * items longer and may use more memory during its execution.
 * @param transform the suspendable function to turn an upstream item into a [Flow]
 */
fun <T, R> Flow<T>.concatMapEager(transform: suspend (T) -> Flow<R>): Flow<R> =
    concatMapEagerInternal(transform)

internal fun <T, R> Flow<T>.concatMapEagerInternal(transform: suspend (T) -> Flow<R>): Flow<R> = flow {
    coroutineScope {
        val resumeOutput = Resumable()
        val innerQueues = ConcurrentLinkedQueue<ConcatMapEagerInnerQueue<R>>()
        val innerDone = atomic(false)

        launch {
            try {
                collect { item ->
                    log.trace { "source item=$item" }
                    val f = transform(item)
                    val newQueue = ConcatMapEagerInnerQueue<R>()
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

        var innerQueue: ConcatMapEagerInnerQueue<R>? = null
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
                    emit(value)
                    continue
                }
            }
            // 다음 item이 올때까지 대기한다
            resumeOutput.await()
        }
    }
}

class ConcatMapEagerInnerQueue<R> {
    val queue = ConcurrentLinkedQueue<R>()
    val done = atomic(false)
}
