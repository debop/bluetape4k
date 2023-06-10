@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowAmbIterable
import kotlinx.coroutines.flow.Flow

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the [Iterable] sequence of [Flow]s
 */
fun <T> Iterable<Flow<T>>.amb(): Flow<T> = FlowAmbIterable(this)

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the array of [Flow]s
 */
fun <T> ambFlowOf(vararg sources: Flow<T>): Flow<T> = FlowAmbIterable(*sources)
