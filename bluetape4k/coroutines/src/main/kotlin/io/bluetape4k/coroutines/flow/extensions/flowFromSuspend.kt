@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * Creates a _cold_ flow that produces a single value from the given [function].
 *
 * Example of usage:
 *
 * ```
 * suspend fun remoteCall(): R = ...
 * fun remoteCallFlow(): Flow<R> = flowFromSuspend(::remoteCall)
 * ```
 * @param function the function that produces a single value
 */
fun <T> flowFromSuspend(function: suspend () -> T): Flow<T> = FlowFromSuspend(function)

private class FlowFromSuspend<T>(private val supplier: suspend () -> T): Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        collector.emit(supplier())
    }
}
