package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowConcatArrayEager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList


/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> Iterable<Flow<T>>.concatFlows(): Flow<T> = FlowConcatArrayEager(this.toList())

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
suspend fun <T> Flow<Flow<T>>.concatFlows(): Flow<T> = FlowConcatArrayEager(this.toList())

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> concatArrayEager(vararg sources: Flow<T>): Flow<T> = FlowConcatArrayEager(*sources)
