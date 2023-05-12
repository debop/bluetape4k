package io.bluetape4k.concurrent

import io.bluetape4k.core.requireInRange
import io.bluetape4k.core.requirePositiveNumber
import io.bluetape4k.logging.KLogging
import kotlinx.atomicfu.atomic

class AtomicIntRoundrobin private constructor(val maximum: Int) {

    companion object: KLogging() {
        private const val DEFAULT_MAXIMUM = 16

        @JvmStatic
        operator fun invoke(maximum: Int = DEFAULT_MAXIMUM): AtomicIntRoundrobin {
            maximum.requirePositiveNumber("maximum")
            return AtomicIntRoundrobin(maximum)
        }
    }

    private val valueSequencer = atomic(0)
    private var currentValue: Int by valueSequencer

    private val synchronizedObject = Any()

    fun get(): Int = currentValue

    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        currentValue = value
    }

    fun next(): Int = synchronized(synchronizedObject) {
        valueSequencer.incrementAndGet()
        if (currentValue >= maximum) {
            valueSequencer.value = 0
        }
        currentValue
    }
}
