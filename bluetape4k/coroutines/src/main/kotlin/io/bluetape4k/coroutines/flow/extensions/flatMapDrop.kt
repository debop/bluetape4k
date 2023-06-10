package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowFlatMapDrop
import kotlinx.coroutines.flow.Flow

/**
 * Maps items from the upstream to [Flow] and relays its items while dropping upstream items
 * until the current inner [Flow] completes.
 */
fun <T, R> Flow<T>.flatMapDrop(mapper: suspend (T) -> Flow<R>): Flow<R> = FlowFlatMapDrop(this, mapper)
