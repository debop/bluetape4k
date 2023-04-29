package io.bluetape4k.collections.eclipse.parallel

import java.util.concurrent.Executor
import org.eclipse.collections.impl.parallel.ParallelMapIterate

fun <K, V> Map<K, V>.parForEach(block: (key: K, value: V) -> Unit) {
    ParallelMapIterate.forEachKeyValue(this) { key: K, value: V ->
        block(key, value)
    }
}

fun <K, V, R> Map<K, V>.parMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    mapper: (key: K, value: V) -> R,
): Collection<R> {
    return asIterable()
        .parMap(batchSize, executor, reorder) {
            mapper(it.key, it.value)
        }
}

fun <K, V, R> Map<K, V>.parFlatMap(
    batchSize: Int = DEFAULT_BATCH_SIZE,
    executor: Executor = EXECUTOR_SERVICE,
    reorder: Boolean = false,
    flatMapper: (key: K, value: V) -> Collection<R>,
): Collection<R> {
    return asIterable()
        .parFlatMap(batchSize, executor, reorder) {
            flatMapper(it.key, it.value)
        }
}
