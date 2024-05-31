package io.bluetape4k.coroutines.flow.extensions.parallel

import kotlinx.coroutines.flow.FlowCollector

/**
 * Reduces the source items into a single value on each rail
 * and emits those.
 */
internal class FlowParallelReduce<T, R>(
    private val source: ParallelFlow<T>,
    private val seed: suspend () -> R,
    private val combine: suspend (R, T) -> R,
): ParallelFlow<R> {
    override val parallelism: Int
        get() = source.parallelism

    override suspend fun collect(vararg collectors: FlowCollector<R>) {
        val n = parallelism
        val rails = Array(n) { ReducerCollector(combine) }

        // Array constructor doesn't support suspendable initializer?
        repeat(n) {
            rails[it].accumulator = seed()
        }

        source.collect(*rails)

        repeat(n) {
            collectors[it].emit(rails[it].accumulator)
        }
    }

    class ReducerCollector<T, R>(private val combine: suspend (R, T) -> R): FlowCollector<T> {

        @Suppress("UNCHECKED_CAST")
        var accumulator: R = null as R

        override suspend fun emit(value: T) {
            accumulator = combine(accumulator, value)
        }
    }
}
