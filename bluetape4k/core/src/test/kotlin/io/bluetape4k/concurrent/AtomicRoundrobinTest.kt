package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AtomicRoundrobinTest {

    companion object: KLogging()

    @Test
    fun `병렬에서 Atomic 한가`() {
        val size = 10000
        val atomic = AtomicIntRoundrobin(size)

        val ids = List(size) { it }.parallelStream().map { atomic.next() }.toList()
        ids.size shouldBeEqualTo size
        ids.toSet().size shouldBeEqualTo size
    }

    @Test
    fun `increment round robin`() {
        val atomic = AtomicIntRoundrobin(4)

        atomic.get() shouldBeEqualTo 0

        val nums = List(8) { atomic.next() }
        nums shouldBeEqualTo listOf(1, 2, 3, 0, 1, 2, 3, 0)
    }

    @Test
    fun `set new value`() {

        val atomic = AtomicIntRoundrobin(16)

        atomic.next() shouldBeEqualTo 1
        atomic.next() shouldBeEqualTo 2

        atomic.set(1)

        atomic.get() shouldBeEqualTo 1
        atomic.next() shouldBeEqualTo 2

    }

    @Test
    fun `set invalid new value`() {
        val atomic = AtomicIntRoundrobin(16)

        assertFailsWith<IllegalArgumentException> {
            atomic.set(Int.MAX_VALUE)
        }

        assertFailsWith<IllegalArgumentException> {
            atomic.set(Int.MIN_VALUE)
        }
    }
}
