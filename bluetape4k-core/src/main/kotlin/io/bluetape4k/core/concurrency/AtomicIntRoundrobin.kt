package io.bluetape4k.core.concurrency

import io.bluetape4k.core.requireInRange
import io.bluetape4k.core.requirePositiveNumber
import io.bluetape4k.logging.KLogging
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater

class AtomicIntRoundrobin private constructor(val maximum: Int) {
    companion object : KLogging() {

        @JvmStatic
        @JvmOverloads
        operator fun invoke(maximum: Int = 16): AtomicIntRoundrobin {
            maximum.requirePositiveNumber("maximum")
            return AtomicIntRoundrobin(maximum)
        }
    }

    @Volatile
    private var currentValue: Int = 0

    private val updater =
        AtomicIntegerFieldUpdater.newUpdater(AtomicIntRoundrobin::class.java, "currentValue")

    fun get(): Int = currentValue

    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        updater.set(this, value)
    }

    fun next(): Int {
        if (maximum <= 1) {
            return 0
        }
        while (true) {
            val current = get()
            val next = if (current == maximum - 1) 0 else current + 1
            if (updater.compareAndSet(this, current, next)) {
                return next
            }
        }
    }
}
