@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> Iterable<Flow<T>>.merge(): Flow<T> = mergeInternal(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> merge(vararg sources: Flow<T>): Flow<T> = mergeInternal(sources.asList())

/**
 * Merges an array of Flow instances in an unbounded manner.
 */
internal fun <T> mergeInternal(sources: List<Flow<T>>): Flow<T> = channelFlow {
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
                value != null           -> send(value)
                else                    -> ready.await()
            }
        }
    }
}
