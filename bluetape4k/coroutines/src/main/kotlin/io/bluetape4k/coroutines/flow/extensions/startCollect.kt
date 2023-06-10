package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowStartCollectOn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

/**
 * Stats collecting the upstream on the specified dispatcher.
 */
fun <T> Flow<T>.startCollectOn(dispatcher: CoroutineDispatcher): Flow<T> =
    FlowStartCollectOn(this, dispatcher)
