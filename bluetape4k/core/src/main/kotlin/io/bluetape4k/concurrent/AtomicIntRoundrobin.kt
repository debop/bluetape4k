package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireInRange
import io.bluetape4k.support.requirePositiveNumber
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.withLock
import java.util.concurrent.locks.ReentrantLock


/**
 * [AtomicIntRoundrobin] 은 [maximum] 값까지의 순환 카운터를 제공합니다.
 *
 * ```
 * val atomic = AtomicIntRoundrobin(4)
 * atomic.get() shouldBeEqualTo 0
 * val nums = List(8) { atomic.next() }  // (1, 2, 3, 0, 1, 2, 3, 0)
 * ```
 *
 * @param maximum 최대 값
 */
class AtomicIntRoundrobin private constructor(val maximum: Int) {

    companion object: KLogging() {
        private const val DEFAULT_MAXIMUM = 16

        @JvmStatic
        operator fun invoke(maximum: Int = DEFAULT_MAXIMUM): AtomicIntRoundrobin {
            maximum.requirePositiveNumber("maximum")
            return AtomicIntRoundrobin(maximum)
        }
    }

    private val currentValue = atomic(0)
    private val lock = ReentrantLock()

    /**
     * 현재 값을 반환합니다.
     */
    fun get(): Int = lock.withLock { currentValue.value }

    /**
     * 현재 값을 설정합니다.
     *
     * @param value 설정 할 값 (0 until maximum)
     */
    fun set(value: Int) {
        value.requireInRange(0, maximum - 1, "value")
        lock.withLock {
            currentValue.value = value
        }
    }

    /**
     * 현재 값보다 증가된 값을 반환합니다. [maximum] 이상이라면 0으로 초기화합니다.
     */
    fun next(): Int = lock.withLock {
        currentValue.incrementAndGet()
        if (currentValue.value >= maximum) {
            currentValue.value = 0
        }
        currentValue.value
    }
}
