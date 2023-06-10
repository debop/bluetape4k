@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.StopException
import io.bluetape4k.coroutines.flow.extensions.internal.checkOwnership
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

/**
 * Represents all of the notifications from the source [Flow] as `value`
 * emissions marked with their original types within [Event] objects.
 *
 * @param T
 * @return
 */
fun <T> Flow<T>.materialize(): Flow<Event<T>> =
    map<T, Event<T>> { Event.Value(it) }
        .onCompletion { if (it == null) emit(Event.Complete) }
        .catch { emit(Event.Error(it)) }

/**
 * Converts a [Flow] of [Event] objects (materialized flow) into the emissions that they represent.
 */
fun <T> Flow<Event<T>>.dematerialize(): Flow<T> = flow {
    try {
        collect {
            when (it) {
                is Event.Value -> emit(it.value)
                is Event.Error -> throw it.error
                Event.Complete -> throw StopException(this)
            }
        }
    } catch (e: StopException) {
        e.checkOwnership(this@flow)
    }
}
