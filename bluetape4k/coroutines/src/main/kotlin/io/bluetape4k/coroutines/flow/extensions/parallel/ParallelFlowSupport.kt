package io.bluetape4k.coroutines.flow.extensions.parallel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll


// -----------------------------------------------------------------------------------------
// Parallel Extensions
// -----------------------------------------------------------------------------------------

/**
 * Consumes the upstream and dispatches individual items to a parallel rail
 * of the parallel flow for further consumption.
 */
fun <T> Flow<T>.parallel(parallelism: Int, runOn: (Int) -> CoroutineDispatcher): ParallelFlow<T> =
    FlowParallel(this, parallelism, runOn)

/**
 * Consumes the parallel upstream and turns it into a sequential flow again.
 */
fun <T> ParallelFlow<T>.sequential(): Flow<T> =
    FlowSequential(this)

/**
 * Maps the values of the upstream in parallel.
 */
fun <T, R> ParallelFlow<T>.map(mapper: suspend (T) -> R): ParallelFlow<R> =
    FlowParallelMap(this, mapper)

/**
 * Filters the values of the upstream in parallel.
 */
fun <T> ParallelFlow<T>.filter(predicate: suspend (T) -> Boolean): ParallelFlow<T> =
    FlowParallelFilter(this, predicate)

/**
 * Transform each upstream item into zero or more emits for the downstrea in parallel.
 */
fun <T, R> ParallelFlow<T>.transform(callback: suspend FlowCollector<R>.(T) -> Unit): ParallelFlow<R> =
    FlowParallelTransform(this, callback)


/**
 * Maps the upstream value on each rail onto a Flow and emits their values in order on the same rail.
 */
@ExperimentalCoroutinesApi
fun <T, R> ParallelFlow<T>.concatMap(mapper: suspend (T) -> Flow<R>): ParallelFlow<R> =
    FlowParallelTransform(this) {
        emitAll(mapper(it))
    }

/**
 * Reduces the source items into a single value on each rail and emits those.
 */
fun <T, R> ParallelFlow<T>.reduce(seed: suspend () -> R, combine: suspend (R, T) -> R): ParallelFlow<R> =
    FlowParallelReduce(this, seed, combine)

/**
 * Reduce the values within the parallel rails and then reduce the rails to a single result value.
 */
fun <T> ParallelFlow<T>.reduce(combine: suspend (T, T) -> T): Flow<T> =
    FlowParallelReduceSequential(this, combine)
