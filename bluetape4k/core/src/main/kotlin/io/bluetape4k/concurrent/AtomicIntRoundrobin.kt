package io.bluetape4k.concurrent

import io.bluetape4k.core.requireInRange
import io.bluetape4k.core.requirePositiveNumber
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic

class AtomicIntRoundrobin private constructor(val maximum: Int) {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(maximum: Int = 16): AtomicIntRoundrobin {
            maximum.requirePositiveNumber("maximum")
            return AtomicIntRoundrobin(maximum)
        }
    }

    private val valueSequencer = atomic(0)
    private var currentValue: Int by valueSequencer

    fun get(): Int = currentValue

    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        currentValue = value
    }

    fun next(): Int = synchronized(this) {
        valueSequencer.incrementAndGet()
        if (currentValue >= maximum) {
            valueSequencer.value = 0
        }
        currentValue
    }
}
