package io.bluetape4k.coroutines.flow.extensions.parallel

import kotlinx.coroutines.flow.FlowCollector

/**
 * Filters values in parallel.
 */
internal class FlowParallelFilter<T>(
    private val source: ParallelFlow<T>,
    private val predicate: suspend (T) -> Boolean
): ParallelFlow<T> {

    override val parallelism: Int
        get() = source.parallelism

    override suspend fun collect(vararg collectors: FlowCollector<T>) {
        val n = parallelism
        val rails = Array(n) { FilterCollector(collectors[it], predicate) }

        source.collect(*rails)
    }

    class FilterCollector<T>(
        val collector: FlowCollector<T>,
        val predicate: suspend (T) -> Boolean
    ): FlowCollector<T> {
        override suspend fun emit(value: T) {
            if (predicate(value)) {
                collector.emit(value)
            }
        }
    }
}
