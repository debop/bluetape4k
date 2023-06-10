package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.core.requireGe
import io.bluetape4k.core.requireGt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach


/**
 * Flow 요소들을 windowing 을 수행하여 `Flow<List<T>>` 로 변환합니다.
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val windowed = flow.windowed(3, 1)   // {1,2,3}, {2,3,4}, {3,4,5}, {4,5}, {5}
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
 * val windowed = flow.windowedFlow(3, 1)   // {1,2,3}, {2,3,4}, {3,4,5}, {4,5}, {5}
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
