@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


/**
 * Projects each source value to a [Flow] which is merged in the output [Flow] only if the previous projected [Flow] has completed.
 * If value is received while there is some projected [Flow] sequence being merged, it will simply be ignored.
 *
 * This method is a shortcut for `map(transform).flattenFirst()`. See [flattenFirst].
 *
 * ### Operator fusion
 *
 * Applications of [flowOn], [buffer], and [produceIn] _after_ this operator are fused with
 * its concurrent merging so that only one properly configured channel is used for execution of merging logic.
 *
 * @param transform A transform function to apply to value that was observed while no Flow is executing in parallel.
 */
fun <T, R> Flow<T>.flatMapFirst(transform: suspend (value: T) -> Flow<R>): Flow<R> =
    map(transform).flattenFirst()

/**
 * This function is an alias to [flatMapFirst] operator.
 */
fun <T, R> Flow<T>.exhaustMap(transform: suspend (value: T) -> Flow<R>): Flow<R> =
    flatMapFirst(transform)

/**
 * This function is an alias to [flattenFirst] operator.
 */
fun <T> Flow<Flow<T>>.exhaustAll(): Flow<T> = flattenFirst()

/**
 * Converts a higher-order [Flow] into a first-order [Flow] by dropping inner [Flow]
 * while the previous inner [Flow] has not yet completed.
 */
fun <T> Flow<Flow<T>>.flattenFirst(): Flow<T> = channelFlow {
    val busy = atomic(false)

    collect { inner ->
        if (busy.compareAndSet(expect = false, update = true)) {
            // Do not pay for dispatch here, it's never necessary
            launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    inner.collect { send(it) }
                    busy.value = false
                } catch (e: CancellationException) {
                    busy.value = false
                }
            }
        }
    }
}
