@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow


/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 *
 * @receiver the [Iterable] sequence of [Flow]s
 */
fun <T> Iterable<Flow<T>>.race(): Flow<T> = amb()


/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 */
fun <T> race(flow1: Flow<T>, flow2: Flow<T>, vararg flows: Flow<T>): Flow<T> = amb(flow1, flow2, *flows)

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 */
fun <T> Flow<T>.raceWith(flow1: Flow<T>, vararg flows: Flow<T>): Flow<T> = amb(this, flow1, *flows)
