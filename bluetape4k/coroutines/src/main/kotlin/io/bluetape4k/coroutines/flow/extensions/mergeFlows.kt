@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> Iterable<Flow<T>>.mergeFlows(): Flow<T> = mergeFlowsInternal(this.toList())
// FlowMergeArray(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
suspend fun <T> Flow<Flow<T>>.mergeFlows(): Flow<T> = mergeFlowsInternal(this.toList()) // FlowMergeArray(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> mergeFlows(vararg sources: Flow<T>): Flow<T> = mergeFlowsInternal(sources.asList())// FlowMergeArray(*sources)

/**
 * Merges an array of Flow instances in an unbounded manner.
 */
internal fun <T> mergeFlowsInternal(sources: List<Flow<T>>): Flow<T> = flow {
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
                value != null           -> emit(value)
                else                    -> ready.await()
            }
        }
    }
}
