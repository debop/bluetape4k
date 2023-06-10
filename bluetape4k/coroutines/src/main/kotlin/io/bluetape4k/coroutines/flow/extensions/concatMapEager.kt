@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowConcatMapEager
import kotlinx.coroutines.flow.Flow

/**
 * Maps the upstream values into [Flow]s and launches them all at once, then
 * emits items from a source before items of the next are emitted.
 * Note that the upstream and each source is consumed in an unbounded manner and thus,
 * depending on the speed of the current source and the collector, the operator may retain
 * items longer and may use more memory during its execution.
 * @param mapper the suspendable function to turn an upstream item into a [Flow]
 */
fun <T, R> Flow<T>.concatMapEager(mapper: suspend (T) -> Flow<R>): Flow<R> =
    FlowConcatMapEager(this, mapper)
