package io.bluetape4k.coroutines.flow.extensions.parallel

import kotlinx.coroutines.flow.FlowCollector

/**
 * Base interface for parallel stages in an operator
 */
interface ParallelFlow<out T> {

    val parallelism: Int

    suspend fun collect(vararg collectors: FlowCollector<T>)
}
