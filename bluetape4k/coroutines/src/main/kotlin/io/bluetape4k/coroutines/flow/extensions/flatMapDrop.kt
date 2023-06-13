@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Maps items from the upstream to [Flow] and relays its items while dropping upstream items
 * until the current inner [Flow] completes.
 */
fun <T, R> Flow<T>.flatMapDrop(transform: suspend (value: T) -> Flow<R>): Flow<R> =
    flatMapFirst(transform)
