package io.bluetape4k.collections.eclipse.parallel

import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap
import org.eclipse.collections.api.map.primitive.ObjectLongMap
import org.eclipse.collections.api.multimap.MutableMultimap
import org.eclipse.collections.impl.parallel.ParallelIterate
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

const val DEFAULT_BATCH_SIZE: Int = 10_000
val AVAILABLE_PROCESSORS: Int by lazy { Runtime.getRuntime().availableProcessors() }
val EXECUTOR_SERVICE: ForkJoinPool by lazy { ForkJoinPool.commonPool() }


@JvmOverloads
inline fun <T> Iterable<T>.parFilter(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline predicate: (T) -> Boolean,
): Collection<T> =
    ParallelIterate.select(
        this,
        { predicate(it) },
        mutableListOf(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <T> Iterable<T>.parReject(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline predicate: (T) -> Boolean,
): Collection<T> =
    ParallelIterate.reject(
        this,
        { predicate(it) },
        mutableListOf(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <T> Iterable<T>.parCount(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = ForkJoinPool.commonPool(),
    crossinline predicate: (T) -> Boolean,
): Int =
    ParallelIterate.count(
        this,
        { predicate(it) },
        batchSize,
        executor
    )


@JvmOverloads
inline fun <T> Iterable<T>.parForEach(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    crossinline block: (T) -> Unit,
) {
    ParallelIterate.forEach(
        this,
        { block(it) },
        batchSize,
        executor
    )
}


@JvmOverloads
inline fun <T> Iterable<T>.parForEachWithIndex(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    taskCount: Int = AVAILABLE_PROCESSORS,
    crossinline block: (index: Int, elem: T) -> Unit,
) {
    ParallelIterate.forEachWithIndex<T, ObjectIntProcedure<T>>(
        this,
        ObjectIntProcedure { value: T, index -> block(index, value) },
        batchSize,
        taskCount
    )
}


@JvmOverloads
inline fun <R> IntArray.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline mapper: (Int) -> R,
): Collection<R> =
    ParallelIterate.collect(
        this.asIterable(),
        { mapper(it) },
        ArrayList(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <R> LongArray.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline mapper: (Long) -> R,
): Collection<R> =
    ParallelIterate.collect(
        this.asIterable(),
        { mapper(it) },
        ArrayList(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <R> FloatArray.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline mapper: (Float) -> R,
): Collection<R> =
    ParallelIterate.collect(
        this.asIterable(),
        { mapper(it) },
        ArrayList(),
        batchSize,
        executor,
        reorder
    )

@JvmOverloads
inline fun <R> DoubleArray.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline mapper: (Double) -> R,
): Collection<R> =
    ParallelIterate.collect(
        this.asIterable(),
        { mapper(it) },
        ArrayList(),
        batchSize,
        executor,
        reorder
    )


/**
 * Java parallelStream()이 성능이 더 좋습니다.
 */
@JvmOverloads
inline fun <T, R> Iterable<T>.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline mapper: (T) -> R,
): Collection<R> =
    ParallelIterate.collect(
        this,
        { mapper(it) },
        ArrayList(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <T, R> Iterable<T>.parFlatMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline flatMapper: (T) -> Collection<R>,
): Collection<R> =
    ParallelIterate.flatCollect(
        this,
        { flatMapper(it) },
        mutableListOf(),
        batchSize,
        executor,
        reorder
    )


@JvmOverloads
inline fun <T, R> Iterable<T>.parFilterMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    crossinline predicate: (T) -> Boolean,
    crossinline mapper: (T) -> R,
): Collection<R> =
    ParallelIterate.collectIf(
        this,
        { predicate(it) },
        { mapper(it) },
        mutableListOf(),
        batchSize,
        executor,
        reorder
    )


inline fun <K, V, R : MutableMultimap<K, V>> Iterable<V>.parGroupBy(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    crossinline keySelector: (V) -> K,
): MutableMultimap<K, V> =
    ParallelIterate.groupBy(
        this,
        { keySelector(it) },
        batchSize,
        executor
    )


@JvmOverloads
inline fun <T, K, V> Iterable<T>.parAggregateBy(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    crossinline groupBy: (T) -> K,
    crossinline zeroValueFactory: () -> V,
    crossinline nonMutatingAggregator: (V, T) -> V,
): MutableMap<K, V> =
    ParallelIterate.aggregateBy(
        this,
        { groupBy(it) },
        { zeroValueFactory() },
        { v: V, t: T -> nonMutatingAggregator(v, t) },
        batchSize,
        executor
    )


@JvmOverloads
inline fun <T, K, V> Iterable<T>.parAggregateInPlaceBy(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    crossinline groupBy: (T) -> K,
    crossinline zeroValueFactory: () -> V,
    crossinline mutatingAggregator: (V, T) -> Unit,
): MutableMap<K, V> =
    ParallelIterate.aggregateInPlaceBy(
        this,
        { groupBy(it) },
        { zeroValueFactory() },
        { v: V, t: T -> mutatingAggregator(v, t) },
        batchSize,
        executor
    )


inline fun <T, V> Iterable<T>.parSumByDouble(
    crossinline groupBy: (T) -> V,
    crossinline func: (T) -> Double,
): ObjectDoubleMap<V> =
    ParallelIterate.sumByDouble(this, { groupBy(it) }, { func(it) })


inline fun <T, V> Iterable<T>.parSumByFloat(
    crossinline groupBy: (T) -> V,
    crossinline func: (T) -> Float,
): ObjectDoubleMap<V> =
    ParallelIterate.sumByFloat(this, { groupBy(it) }, { func(it) })


inline fun <T, V> Iterable<T>.parSumByLong(
    crossinline groupBy: (T) -> V,
    crossinline func: (T) -> Long,
): ObjectLongMap<V> =
    ParallelIterate.sumByLong(this, { groupBy(it) }, { func(it) })


inline fun <T, V> Iterable<T>.parSumByInt(
    crossinline groupBy: (T) -> V,
    crossinline func: (T) -> Int,
): ObjectLongMap<V> =
    ParallelIterate.sumByInt(this, { groupBy(it) }, { func(it) })
