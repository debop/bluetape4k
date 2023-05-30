package io.bluetape4k.coroutines.support

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.stream.consumeAsFlow
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

/**
 * Java Stream 을 [Flow] 처럼 사용합니다.
 *
 * @see [consumeAsFlow]
 *
 * @param T
 * @return
 */
fun <T> Stream<T>.asFlow(): Flow<T> = consumeAsFlow()

suspend fun <T> Stream<T>.coForEach(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    consumer: suspend (T) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .collect { consumer(it) }
}

inline fun <T, R> Stream<T>.coMap(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline transform: suspend (T) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .collect { send(transform(it)) }
}

fun IntStream.consumeAsFlow(): Flow<Int> = IntStreamFlow(this)

fun IntStream.asFlow(): Flow<Int> = consumeAsFlow()

suspend inline fun IntStream.coForEach(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline consumer: suspend (Int) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

inline fun <R> IntStream.coMap(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline transform: suspend (Int) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(transform(it)) }
        .collect()
}

internal class IntStreamFlow(private val stream: IntStream): Flow<Int> {
    private val consumed = atomic(false)

    override suspend fun collect(collector: FlowCollector<Int>) {
        if (!consumed.compareAndSet(expect = false, update = true))
            error("IntStream.consumeAsFlow can be collected only once")

        stream.use { stream ->
            for (value in stream.iterator()) {
                collector.emit(value)
            }
        }
    }
}

fun LongStream.consumeAsFlow(): Flow<Long> = LongStreamFlow(this)

fun LongStream.asFlow(): Flow<Long> = consumeAsFlow()

suspend inline fun LongStream.coForEach(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline consumer: suspend (Long) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

inline fun <R> LongStream.coMap(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline transform: suspend (Long) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(transform(it)) }
        .collect()
}

internal class LongStreamFlow(private val stream: LongStream): Flow<Long> {
    private val consumed = atomic(false)

    override suspend fun collect(collector: FlowCollector<Long>) {
        if (!consumed.compareAndSet(expect = false, update = true))
            error("LongStream.consumeAsFlow can be collected only once")

        stream.use { stream ->
            for (value in stream.iterator()) {
                collector.emit(value)
            }
        }
    }
}

fun DoubleStream.consumeAsFlow(): Flow<Double> = DoubleStreamFlow(this)

fun DoubleStream.asFlow(): Flow<Double> = consumeAsFlow()

suspend inline fun DoubleStream.coForEach(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline consumer: suspend (Double) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

inline fun <R> DoubleStream.coMap(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    crossinline mapper: suspend (Double) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(mapper(it)) }
        .collect()
}

internal class DoubleStreamFlow(private val stream: DoubleStream): Flow<Double> {
    private val consumed = atomic(false)

    override suspend fun collect(collector: FlowCollector<Double>) {
        if (!consumed.compareAndSet(expect = false, update = true))
            error("LongStream.consumeAsFlow can be collected only once")

        stream.use { stream ->
            for (value in stream.iterator()) {
                collector.emit(value)
            }
        }
    }
}
