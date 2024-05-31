package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector


/**
 * Creates a _cold_ flow that produces a single value from the given [function].
 *
 * Example of usage:
 *
 * ```
 * fun remoteCall(): R = ...
 * fun remoteCallFlow(): Flow<R> = flowFromSupplier(::remoteCall)
 * ```
 * @param function the function that produces a single value
 */
fun <T> flowFromSupplier(function: () -> T): Flow<T> = FlowFromSupplier(function)

private class FlowFromSupplier<T>(private val supplier: () -> T): Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        collector.emit(supplier())
    }
}
