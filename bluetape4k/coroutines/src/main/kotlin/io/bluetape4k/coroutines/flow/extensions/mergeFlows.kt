package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowMergeArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> Iterable<Flow<T>>.mergeFlows(): Flow<T> = FlowMergeArray(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
suspend fun <T> Flow<Flow<T>>.mergeFlows(): Flow<T> = FlowMergeArray(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> mergeFlows(vararg sources: Flow<T>): Flow<T> = FlowMergeArray(*sources)
