package io.bluetape4k.kotlinx.coroutines.support

import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream
import java.util.stream.Stream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.stream.consumeAsFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    consumer: suspend (T) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

fun <T, R> Stream<T>.coMap(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    mapper: suspend (T) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(mapper(it)) }
        .collect()
}


fun IntStream.consumeAsFlow(): Flow<Int> = IntStreamFlow(this)

fun IntStream.asFlow(): Flow<Int> = consumeAsFlow()


suspend fun IntStream.coForEach(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    consumer: suspend (Int) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

fun <R> IntStream.coMap(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    mapper: suspend (Int) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(mapper(it)) }
        .collect()
}

private class IntStreamFlow(private val stream: IntStream): Flow<Int> {
    private val consumed = AtomicBoolean(false)

    override suspend fun collect(collector: FlowCollector<Int>) {
        if (!consumed.compareAndSet(false, true))
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

suspend fun LongStream.coForEach(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    consumer: suspend (Long) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

fun <R> LongStream.coMap(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    mapper: suspend (Long) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(mapper(it)) }
        .collect()
}

private class LongStreamFlow(private val stream: LongStream): Flow<Long> {
    private val consumed = AtomicBoolean(false)

    override suspend fun collect(collector: FlowCollector<Long>) {
        if (!consumed.compareAndSet(false, true))
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

suspend fun DoubleStream.coForEach(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    consumer: suspend (Double) -> Unit,
) {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { consumer(it) }
        .collect()
}

fun <R> DoubleStream.coMap(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    mapper: suspend (Double) -> R,
): Flow<R> = channelFlow {
    consumeAsFlow()
        .buffer()
        .flowOn(coroutineContext)
        .onEach { send(mapper(it)) }
        .collect()
}

private class DoubleStreamFlow(private val stream: DoubleStream): Flow<Double> {
    private val consumed = AtomicBoolean(false)

    override suspend fun collect(collector: FlowCollector<Double>) {
        if (!consumed.compareAndSet(false, true))
            error("LongStream.consumeAsFlow can be collected only once")

        stream.use { stream ->
            for (value in stream.iterator()) {
                collector.emit(value)
            }
        }
    }
}
