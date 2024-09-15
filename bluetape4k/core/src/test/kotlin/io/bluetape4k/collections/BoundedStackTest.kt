package io.bluetape4k.collections

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.test.assertFailsWith

class BoundedStackTest {

    companion object: KLogging()

    @Test
    fun `invalid bound`() {
        assertFailsWith<IllegalArgumentException> {
            BoundedStack<Int>(0)
        }
    }

    @Test
    fun `insert item to stack`() {
        val stack = BoundedStack<Int>(3)
        stack.push(1)
        stack.push(2)
        stack.push(3)

        // 기존 요소를 제거하고 추가한다 
        stack.push(4) shouldBeEqualTo 4
        stack.push(5) shouldBeEqualTo 5

        stack.pop() shouldBeEqualTo 5
        stack.pop() shouldBeEqualTo 4
        stack.pop() shouldBeEqualTo 3

        // 더 이상 요소가 없다
        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
    }

    @Test
    fun `push and pop items to stack in multi-thread`() {
        val stack = BoundedStack<Int>(16)
        val counter = atomic(0)
        val lock = ReentrantLock()

        MultithreadingTester()
            .numThreads(8)
            .roundsPerThread(4)
            .add {
                lock.withLock {
                    stack.push(counter.incrementAndGet())
                }
            }
            .add {
                do {
                    val result = lock.withLock {
                        runCatching { stack.pop() }
                            .onSuccess { counter.decrementAndGet() }
                            .onFailure {
                                log.debug(it) { "Fail to pop." }
                                Thread.sleep(1)
                            }
                    }
                } while (result.isFailure)

            }
            .run()

        counter.value shouldBeEqualTo 0
        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
    }

    @Test
    fun `push items over capacity to stack in multi-thread`() {
        val stack = BoundedStack<Int>(4)
        val counter = atomic(0)
        val lock = ReentrantLock()

        MultithreadingTester()
            .numThreads(8)
            .roundsPerThread(4)
            .add {
                lock.withLock {
                    stack.push(counter.incrementAndGet())
                }
            }
            .run()

        counter.value shouldBeEqualTo 8 * 4

        stack.size shouldBeEqualTo 4
        stack.pop() shouldBeEqualTo 32
        stack.pop() shouldBeEqualTo 31
        stack.pop() shouldBeEqualTo 30
        stack.pop() shouldBeEqualTo 29
        stack.size shouldBeEqualTo 0
    }
}
