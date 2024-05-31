package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.scan
import kotlin.experimental.ExperimentalTypeInference

/**
 * Folds the given flow with [operation], emitting every intermediate result,
 * including the initial value supplied by [initialSupplier] at the collection time.
 *
 * Note that the returned initial value should be immutable (or should not be mutated)
 * as it is shared between different collectors.
 *
 * This is a variant of [scan] that the initial value is lazily supplied,
 * which is useful when the initial value is expensive to create
 * or depends on a logic that should be executed at the collection time (lazy semantics).
 *
 * For example:
 * ```kotlin
 * flowOf(1, 2, 3)
 *     .scanWith({ emptyList<Int>() }) { acc, value -> acc + value }
 *     .toList()
 * ```
 * will produce `[[], [1], [1, 2], [1, 2, 3]]`.
 *
 * Another example:
 * ```kotlin
 * // Logic to calculate initial value (e.g. call API, read from DB, etc.)
 * suspend fun calculateInitialValue(): Int {
 *   println("calculateInitialValue")
 *   delay(1000)
 *   return 0
 * }
 *
 * flowOf(1, 2, 3).scanWith(::calculateInitialValue) { acc, value -> acc + value }
 * ```
 *
 * @param initialSupplier a function that returns the initial (seed) accumulator value for each individual collector.
 * @param operation an accumulator function to be invoked on each item emitted by the current [Flow],
 * whose result will be emitted to collector via [FlowCollector.emit]
 * and used in the next accumulator call.
 */
@OptIn(ExperimentalTypeInference::class)
fun <T, R> Flow<T>.scanWith(
    initialSupplier: suspend () -> R,
    @BuilderInference operation: suspend (acc: R, item: T) -> R,
): Flow<R> = flow {
    return@flow emitAll(scan(initialSupplier(), operation))
}
