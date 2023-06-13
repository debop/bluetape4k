@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.yield

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the [Iterable] sequence of [Flow]s
 */
fun <T> Iterable<Flow<T>>.amb(): Flow<T> = ambInternal(this)
// FlowAmbIterable(this)

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the array of [Flow]s
 */
fun <T> amb(vararg sources: Flow<T>): Flow<T> = ambInternal(sources.asIterable())
// FlowAmbIterable(*sources)

internal fun <T> ambInternal(sources: Iterable<Flow<T>>): Flow<T> = flow {
    coroutineScope {
        val channels = sources.map { flow ->
            // Produce the values using the default (rendezvous) channel
            produce {
                flow.collect {
                    send(it)
                    yield() // Emulate fairness, giving each flow chance to emit
                }
            }
        }

        if (channels.isEmpty()) {
            return@coroutineScope
        }

        channels
            .singleOrNull()
            ?.let { return@coroutineScope emitAll(it) }

        val (winnerIndex, winnerResult) = select {
            channels.forEachIndexed { index, channel ->
                channel.onReceiveCatching {
                    index to it
                }
            }
        }

        channels.forEachIndexed { index, channel ->
            if (index != winnerIndex) {
                channel.cancel()
            }
        }

        winnerResult
            .onSuccess {
                emit(it)
                emitAll(channels[winnerIndex])
            }
            .onFailure {
                it?.let { throw it }
            }
    }
}
