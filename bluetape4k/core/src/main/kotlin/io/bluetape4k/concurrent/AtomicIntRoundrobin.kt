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

    private val _currentValue = atomic(0)
    private val currentValue: Int by _currentValue

    private val synchronizedObject = Any()

    fun get(): Int = currentValue

    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        _currentValue.value = value
    }

    fun next(): Int = synchronized(synchronizedObject) {
        _currentValue.incrementAndGet()
        if (currentValue >= maximum) {
            _currentValue.value = 0
        }
        currentValue
    }
}
