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

    private val currentValue = atomic(0)

    fun get(): Int = currentValue.value

    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        currentValue.value = value
    }

    fun next(): Int {
        if (maximum <= 1) {
            return 0
        }
        while (true) {
            val current = get()
            val next = if (current == maximum - 1) 0 else current + 1
            if (currentValue.compareAndSet(current, next)) {
                return next
            }
        }
    }
}
