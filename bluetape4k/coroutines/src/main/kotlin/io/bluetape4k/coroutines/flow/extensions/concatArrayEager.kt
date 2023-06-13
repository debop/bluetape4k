@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.AtomicIntArray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

private val log = KotlinLogging.logger { }

/**
 * Launches all source flows at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> Iterable<Flow<T>>.concatFlows(): Flow<T> = // FlowConcatArrayEager(this.toList())
    concatArrayEagerInternal(this.toList())

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
suspend fun <T> Flow<Flow<T>>.concatFlows(): Flow<T> = // FlowConcatArrayEager(this.toList())
    concatArrayEagerInternal(this.toList())

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> concatArrayEager(vararg sources: Flow<T>): Flow<T> = // FlowConcatArrayEager(*sources)
    concatArrayEagerInternal(sources.toList())

internal fun <T> concatArrayEagerInternal(sources: List<Flow<T>>): Flow<T> = flow {
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
                emit(value)
                continue
            }
            reader.await()
        }
    }
}
