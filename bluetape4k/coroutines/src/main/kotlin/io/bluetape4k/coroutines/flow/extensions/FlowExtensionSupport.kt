package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowAmbIterable
import io.bluetape4k.coroutines.flow.extensions.internal.FlowConcatArrayEager
import io.bluetape4k.coroutines.flow.extensions.internal.FlowConcatMapEager
import io.bluetape4k.coroutines.flow.extensions.internal.FlowFlatMapDrop
import io.bluetape4k.coroutines.flow.extensions.internal.FlowMergeArray
import io.bluetape4k.coroutines.flow.extensions.internal.FlowMulticastFunction
import io.bluetape4k.coroutines.flow.extensions.internal.FlowOnBackpressureDrop
import io.bluetape4k.coroutines.flow.extensions.internal.FlowStartCollectOn
import io.bluetape4k.coroutines.flow.extensions.internal.FlowTakeUntil
import io.bluetape4k.coroutines.flow.extensions.subject.MulticastSubject
import io.bluetape4k.coroutines.flow.extensions.subject.PublishSubject
import io.bluetape4k.coroutines.flow.extensions.subject.ReplaySubject
import io.bluetape4k.coroutines.flow.extensions.subject.SubjectApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 *
 * Note that due to how coroutines/[Flow] are implemented, it is not guaranteed
 * the [transform] function connects the upstream with the downstream in time,
 * causing item loss or even run-to-completion without any single upstream item
 * being collected and transformed. To avoid such scenarios, use the
 * `publish(expectedCollectors)` overload.
 */
fun <T, R> Flow<T>.publish(transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    FlowMulticastFunction(this, { PublishSubject() }, transform)

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 *
 * Note that due to how coroutines/[Flow] are implemented, it is not guaranteed
 * the [transform] function connects the upstream with the downstream in time,
 * causing item loss or even run-to-completion without any single upstream item
 * being collected and transformed. To avoid such scenarios, specify the
 * [expectedCollectors] to delay the collection of the upstream until the number
 * of inner collectors has reached the specified number.
 *
 * @param expectedCollectors the number of collectors to wait for before resuming the source, allowing
 * the desired number of collectors to arrive and be ready for the upstream items
 */
fun <T, R> Flow<T>.publish(expectedCollectors: Int = 3, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> {
    return FlowMulticastFunction(this, { MulticastSubject(expectedCollectors.coerceAtLeast(1)) }, transform)
}

/**
 * Shares a single collector towards the upstream source and multicasts
 * values to any number of consumers which then can produce the output
 * flow of values.
 */
fun <T, R> Flow<T>.multicast(subjectSupplier: () -> SubjectApi<T>, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    FlowMulticastFunction(this, subjectSupplier, transform)

/**
 * Shares a single collector towards the upstream source and multicasts
 * up to a given [maxSize] number of cached values to any number of
 * consumers which then can produce the output
 * flow of values.
 */
fun <T, R> Flow<T>.replay(transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject() }, transform)

fun <T, R> Flow<T>.replay(maxSize: Int, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxSize) }, transform)

fun <T, R> Flow<T>.replay(maxTimeout: Duration, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxTimeout.toMillis(), TimeUnit.MILLISECONDS) }, transform)

fun <T, R> Flow<T>.replay(maxSize: Int, maxTimeout: Duration, transform: suspend (Flow<T>) -> Flow<R>): Flow<R> =
    replay({ ReplaySubject(maxSize, maxTimeout.toMillis(), TimeUnit.MILLISECONDS) }, transform)

fun <T, R> Flow<T>.replay(
    maxSize: Int,
    maxTimeout: Duration,
    timeSource: (TimeUnit) -> Long,
    transform: suspend (Flow<T>) -> Flow<R>
): Flow<R> =
    replay({ ReplaySubject(maxSize, maxTimeout.toMillis(), TimeUnit.MILLISECONDS, timeSource) }, transform)

fun <T, R> Flow<T>.replay(
    replaySubjectSupplier: () -> ReplaySubject<T>,
    transform: suspend (Flow<T>) -> Flow<R>
): Flow<R> =
    FlowMulticastFunction(this, replaySubjectSupplier, transform)

/**
 * Stats collecting the upstream on the specified dispatcher.
 */
fun <T> Flow<T>.startCollectOn(dispatcher: CoroutineDispatcher): Flow<T> =
    FlowStartCollectOn(this, dispatcher)

fun <T> Flow<T>.concatWithEx(other: Flow<T>): Flow<T> {
    val source = this
    return source.onCompletion { if (it == null) emitAll(other) }
}

/**
 * [other] 소스에서 요소가 emit 되거나 완료될 때까지는 main source로부터 소비한다.
 */
fun <T, U> Flow<T>.takeUntil(other: Flow<U>): Flow<T> =
    FlowTakeUntil(this, other)


fun flowOfRange(start: Int, count: Int): Flow<Int> =
    (start until start + count).asFlow()

fun IntRange.asFlow(): Flow<Int> = flow {
    forEach {
        emit(it)
    }
}

/**
 * Signal 0L after the given time passed
 */
fun timer(timeout: Long, unit: TimeUnit): Flow<Long> =
    flow {
        delay(unit.toMillis(timeout))
        emit(0L)
    }

/**
 * Signal 0L after the given time passed
 */
fun flowOfDelay(initialDelay: Duration): Flow<Long> = flow {
    delay(initialDelay.toMillis().coerceAtLeast(0L))
    emit(0L)
}

/**
 * Drops items from the upstream when the downstream is not ready to receive them.
 */
fun <T> Flow<T>.onBackpressureDrop(): Flow<T> = FlowOnBackpressureDrop(this)

/**
 * Maps items from the upstream to [Flow] and relays its items while dropping upstream items
 * until the current inner [Flow] completes.
 */
fun <T, R> Flow<T>.flatMapDrop(mapper: suspend (T) -> Flow<R>): Flow<R> = FlowFlatMapDrop(this, mapper)

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> Iterable<Flow<T>>.mergeFlows(): Flow<T> = FlowMergeArray(this.toList())

/**
 * Merges multiple sources in an unbounded manner.
 */
fun <T> mergeFlows(vararg sources: Flow<T>): Flow<T> = FlowMergeArray(*sources)

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> Iterable<Flow<T>>.concatFlows(): Flow<T> = FlowConcatArrayEager(this.toList())

/**
 * Launches all [sources] at once and emits all items from a source before items of the next are emitted.
 * Note that each source is consumed in an unbounded manner and thus, depending on the speed of
 * the current source and the collector, the operator may retain items longer and may use more memory
 * during its execution.
 */
fun <T> concatArrayEager(vararg sources: Flow<T>): Flow<T> = FlowConcatArrayEager(*sources)

/**
 * Maps the upstream values into [Flow]s and launches them all at once, then
 * emits items from a source before items of the next are emitted.
 * Note that the upstream and each source is consumed in an unbounded manner and thus,
 * depending on the speed of the current source and the collector, the operator may retain
 * items longer and may use more memory during its execution.
 * @param mapper the suspendable function to turn an upstream item into a [Flow]
 */
fun <T, R> Flow<T>.concatMapEager(mapper: suspend (T) -> Flow<R>): Flow<R> =
    FlowConcatMapEager(this, mapper)

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
