package io.bluetape4k.coroutines.flow

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.core.requireGe
import io.bluetape4k.core.requireGt
import io.bluetape4k.core.requireLe
import io.bluetape4k.coroutines.RingBuffer
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

fun charFlowOf(start: Char, endInclusive: Char, step: Int = 1): Flow<Char> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun byteFlowOf(start: Byte, endInclusive: Byte, step: Byte = 1): Flow<Byte> = flow {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        emit(current)
        current = (current + step).toByte()
    }
}

fun intFlowOf(start: Int, endInclusive: Int, step: Int = 1): Flow<Int> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun longFlowOf(start: Long, endInclusive: Long, step: Long = 1L): Flow<Long> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun floatFlowOf(start: Float, endInclusive: Float, step: Float = 1.0F): Flow<Float> = flow {
    start.requireLe(endInclusive, "start")

    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

fun doubleFlowOf(start: Double, endInclusive: Double, step: Double = 1.0): Flow<Double> = flow {
    start.requireLe(endInclusive, "start")
    var current = start
    while (current <= endInclusive) {
        emit(current)
        current += step
    }
}

/**
 * [times]만큼 반복해서 [func]을 수행하며, 결과를 flow 로 emit 합니다.
 *
 * @param T
 * @param times 반복 횟수 (0보다 커야 함)
 * @param func 반복 실행할 function
 * @return
 */
inline fun <T> repeatFlow(times: Int, crossinline func: suspend (index: Int) -> T): Flow<T> = flow {
    times.assertPositiveNumber("times")

    repeat(times) { index ->
        emit(func(index))
    }
}

/**
 * flow의 요소들을 [concurrency]만큼 병렬로 [transform]을 수행하여 처리속도를 높힙니다.
 *
 * @param coroutineContext Coroutine context
 * @param concurrency 동시 실행할 숫자
 * @param transform  변환 함수
 */
inline fun <T, R> Flow<T>.mapParallel(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    concurrency: Int = DEFAULT_CONCURRENCY,
    crossinline transform: suspend (value: T) -> R,
): Flow<R> {
    concurrency.assertPositiveNumber("concurrency")

    return flatMapMerge(concurrency) { value ->
        flow { emit(transform(value)) }.flowOn(coroutineContext)
    }
}

/**
 * Flow 를 [size] 만큼씩 조각내어 `Flow<List<T>>` 로 변한합니다.
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val chunked = flow.chunked(3)   // {1,2,3}, {4,5}
 * ```

 *
 * @param size chunk size. (require greater than 0)
 */
fun <T> Flow<T>.chunked(size: Int): Flow<List<T>> = windowed(size, size)

/**
 * Flow 요소들을 sliding 방식으로 요소를 선택해서 제공합니다.
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val sliding = flow.sliding(3)   // {1,2,3}, {2,3,4}, {3,4,5}, {4,5}, {5}
 * ```
 * @param size sliding size. (require greater than 0)
 */
fun <T> Flow<T>.sliding(size: Int): Flow<List<T>> = windowed(size, 1)

/**
 * Flow 요소들을 windowing 을 수행하여 `Flow<List<T>>` 로 변환합니다.
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val windowed = flow.sliding(3, 1)   // {1,2,3}, {2,3,4}, {3,4,5}, {4,5}, {5}
 * ```
 * @param size window size (require positive number)
 * @param step step (require positive number)
 */
fun <T> Flow<T>.windowed(size: Int, step: Int): Flow<List<T>> = flow {
    size.requireGt(0, "size")
    step.requireGt(0, "step")
    size.requireGe(step, "step")


    var elements = mutableListOf<T>()
    val counter = atomic(0)

    this@windowed.onEach { element ->
        elements.add(element)
        if (counter.incrementAndGet() == size) {
            emit(elements.toList())
            elements = elements.drop(step).toMutableList()
            counter.addAndGet(-step)
        }
    }.collect()

    while (counter.value > 0) {
        emit(elements.take(step))
        elements = elements.drop(step).toMutableList()
        counter.addAndGet(-step)
    }
}

/**
 * Flow 요소들을 windowing 을 수행하여 `Flow<Flow<T>>` 로 변환합니다.
 *
 * @see [windowed]
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val windowed = flow.sliding(3, 1)   // {1,2,3}, {2,3,4}, {3,4,5}, {4,5}, {5}
 * ```
 * @param size window size (require positive number)
 * @param step step (require positive number)
 */
fun <T> Flow<T>.windowedFlow(size: Int, step: Int): Flow<Flow<T>> = channelFlow {
    size.requireGt(0, "size")
    step.requireGt(0, "step")
    size.requireGe(step, "step")

    var elements = mutableListOf<T>()
    val counter = atomic(0)

    this@windowedFlow.collect { element ->
        elements.add(element)
        if (counter.incrementAndGet() == size) {
            send(elements.asFlow())
            elements = elements.drop(step).toMutableList()
            counter.addAndGet(-step)
        }
    }

    while (counter.value > 0) {
        send(elements.take(step).asFlow())
        elements = elements.drop(step).toMutableList()
        counter.addAndGet(-step)
    }
}

/**
 * [groupSelector] 로 얻은 값이 이전 값과 같은 경우를 묶어서 Flow로 전달하려고 할 때 사용합니다.
 * One-To-Many 정보의 ResultSet 을 묶을 때 사용합니다.
 *
 * 참고: [Spring R2DBC OneToMany RowMapping](https://heesutory.tistory.com/33)
 */
fun <T, V: Any> Flow<T>.bufferUntilChanged(groupSelector: (T) -> V): Flow<List<T>> = channelFlow {
    // HINT: kotlin-flow-extensions 에 있는 groupBy 사용
    //    return this@bufferUntilChanged
    //        .groupBy { groupSelector(it) }
    //        .flatMapMerge { it.toList() }


    val elements = mutableListOf<T>()
    var prevGroup: V? = null

    this@bufferUntilChanged.collect { element ->
        val currentGroup = groupSelector(element)
        if (prevGroup == null) {
            prevGroup = currentGroup
        }
        if (prevGroup == currentGroup) {
            elements.add(element)
        } else {
            send(elements.toList())
            elements.clear()
            elements.add(element)
            prevGroup = currentGroup
        }
    }
    if (elements.isNotEmpty()) {
        send(elements)
    }
}

/**
 * [size] 만큼이 채워지기 전까지는 현재 요소만 반환하고, 모든 요소가 채워지면, sliding으로 진행한다
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val sliding = flow.bufferedSliding(3)   // {1}, {1,2}, {1,2,3}, {2,3,4}, {3,4,5}
 * ```
 *
 * @param T
 * @param size sliding size. (require greater than 0)
 * @return
 */
fun <T> Flow<T>.bufferedSliding(size: Int): Flow<List<T>> = channelFlow {
    size.requireGt(1, "window size")
    val ringBuffer = RingBuffer.boxing<T>(size)

    this@bufferedSliding.collect { element ->
        ringBuffer.push(element)
        send(ringBuffer.snapshot())
    }
}
