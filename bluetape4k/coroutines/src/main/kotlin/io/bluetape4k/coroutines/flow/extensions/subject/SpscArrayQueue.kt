package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.atomicArrayOfNulls

/**
 * A Single-Producer Single-Consumer bounded queue.
 */
internal class SpscArrayQueue<T> private constructor(capacity: Int) {

    companion object: KLogging() {

        operator fun <T> invoke(capacity: Int = 1): SpscArrayQueue<T> {
            return SpscArrayQueue(capacity.coerceAtLeast(1))
        }

        private val EMPTY = Any()

        private fun nextPowerOf2(x: Int): Int {
            val h = Integer.highestOneBit(x)
            if (h == x)
                return x
            return h * 2
        }
    }

    private val referenceArray = atomicArrayOfNulls<Any>(nextPowerOf2(capacity))
        .apply {
            repeat(size) {
                get(it).lazySet(EMPTY)
            }
        }

    private var consumerIndex by atomic(0)
    private var producerIndex by atomic(0)

    val isEmpty: Boolean get() = consumerIndex == producerIndex

    fun offer(value: T): Boolean {
        val mask = referenceArray.size - 1
        val pi = producerIndex

        val offset = pi and mask

        if (referenceArray[offset].value == EMPTY) {
            referenceArray[offset].lazySet(value)
            producerIndex = pi + 1
            return true
        }
        return false
    }

    fun poll(out: Array<Any?>): Boolean {
        val mask = referenceArray.size - 1
        val ci = consumerIndex
        val offset = ci and mask

        if (referenceArray[offset].value == EMPTY) {
            return false
        }
        out[0] = referenceArray[offset].value
        referenceArray[offset].lazySet(EMPTY)
        consumerIndex = ci + 1
        return true
    }

    fun clear() {
        val mask = referenceArray.size - 1
        var ci = consumerIndex

        while (true) {
            val offset = ci and mask
            if (referenceArray[offset].value == EMPTY) {
                break
            }
            referenceArray[offset].lazySet(EMPTY)
            ci++
        }
        consumerIndex = ci
    }
}
