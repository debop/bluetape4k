package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.yield

/**
 * Starts collecting all source [Flow]s and relays the items of the first one to emit an item,
 * cancelling the rest.
 * @param sources the array of [Flow]s
 */
@Deprecated("use ambInternal")
internal class FlowAmbIterable<T>(private val sources: Iterable<Flow<T>>): Flow<T> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T> invoke(vararg sources: Flow<T>): FlowAmbIterable<T> {
            return FlowAmbIterable(sources.asIterable())
        }

        @JvmStatic
        operator fun <T> invoke(sources: Collection<Flow<T>>): FlowAmbIterable<T> {
            return FlowAmbIterable(sources)
        }
    }

    override suspend fun collect(collector: FlowCollector<T>) = coroutineScope {
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
            ?.let { return@coroutineScope collector.emitAll(it) }

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
                collector.emit(it)
                collector.emitAll(channels[winnerIndex])
            }
            .onFailure {
                it?.let { throw it }
            }
    }
}
