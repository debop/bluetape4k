package io.bluetape4k.coroutines.flow.extensions.parallel

import kotlinx.coroutines.flow.FlowCollector

/**
 * Transform each upstream item into zero or more emits for the downstream in parallel.
 */
internal class FlowParallelTransform<T, R>(
    private val source: ParallelFlow<T>,
    private val callback: suspend FlowCollector<R>.(T) -> Unit,
): ParallelFlow<R> {

    override val parallelism: Int
        get() = source.parallelism

    override suspend fun collect(vararg collectors: FlowCollector<R>) {
        val n = parallelism
        val rails = Array(n) { OnEachCollector(collectors[it], callback) }

        source.collect(*rails)
    }

    class OnEachCollector<T, R>(
        val collector: FlowCollector<R>,
        val callback: suspend FlowCollector<R>.(T) -> Unit,
    ): FlowCollector<T> {
        override suspend fun emit(value: T) {
            callback(collector, value)
        }
    }
}
