package io.bluetape4k.coroutines.flow.extensions.subject

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.coroutines.tests.withSingleThread
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UnicastSubjectTest {

    companion object: KLogging() {
        val expectedList = (0..4).toFastList()
        const val BUFFER_SIZE = 500_000
    }

    @Test
    fun `offline - after complete`() = runSuspendTest {
        val us = UnicastSubject<Int>()
        repeat(5) {
            us.emit(it)
        }
        us.complete()

        val result = us.toList()

        result shouldBeEqualTo expectedList
        us.collectorCancelled.shouldBeTrue()
    }

    @Test
    fun `offline - after error`() = runSuspendTest {
        val us = UnicastSubject<Int>()
        repeat(5) {
            us.emit(it)
        }
        us.emitError(RuntimeException())
        us.emit(6)

        val result = us.catch { it shouldBeInstanceOf RuntimeException::class }.toList()

        result shouldBeEqualTo listOf(0, 1, 2, 3, 4, 6)
        us.collectorCancelled.shouldBeTrue()
    }

    @Test
    fun `offline - complete 된 flow 에 대해서 다시 collect 를 수행하면 예외가 발생한다`() = runSuspendTest {
        val us = UnicastSubject<Int>()
        repeat(5) {
            us.emit(it)
        }
        us.complete()

        val result = us.toList()

        assertFailsWith<IllegalStateException> {
            us.collect { println(it) }
        }
        result shouldBeEqualTo expectedList
        us.collectorCancelled.shouldBeTrue()
    }

    @Test
    fun `offline - complete 된 unicast subject 로부터 n 개만 take 한다`() = runSuspendTest {
        val us = UnicastSubject<Int>()
        repeat(5) {
            us.emit(it)
        }
        us.complete()

        val result = us.take(3).toList()

        result shouldBeEqualTo listOf(0, 1, 2)
        us.collectorCancelled.shouldBeTrue()
    }

    @Test
    fun `online - basic usage`() = runSuspendTest {
        withSingleThread {
            val us = UnicastSubject<Int>()

            launch {
                us.awaitCollector()

                repeat(5) {
                    us.emit(it)
                }
                us.complete()
            }
            yield()

            val result = us.toList()
            result shouldBeEqualTo expectedList
        }
    }

    @Test
    fun `online - 아주 많은 요소를 emit 하기`() = runSuspendTest {
        withSingleThread {
            val us = UnicastSubject<Int>()

            launch {
                us.awaitCollector()

                repeat(BUFFER_SIZE) {
                    us.emit(it)
                }
                us.complete()
            }
            yield()

            val result = atomic(0)
            us.collect { result.incrementAndGet() }

            result.value shouldBeEqualTo BUFFER_SIZE
        }
    }

    @Test
    fun `online - 몇 개의 요소만 take 하기`() = runSuspendTest {
        withSingleThread {
            val us = UnicastSubject<Int>()

            launch {
                us.awaitCollector()

                repeat(BUFFER_SIZE) {
                    us.emit(it)
                }
                us.complete()
            }
            yield()

            val result = atomic(0)
            us.take(BUFFER_SIZE / 2).collect { result.incrementAndGet() }

            result.value shouldBeEqualTo BUFFER_SIZE / 2
        }
    }

    @Test
    fun `collector 가 시작하기 전에는 버퍼링을 합니다`() = runSuspendTest {
        withSingleThread {
            val us = UnicastSubject<Int>()
            val emitSize = 200

            launch(Dispatchers.IO) {
                us.awaitCollector()

                repeat(emitSize) {
                    us.emit(it)
                    yield()
                }
                us.complete()
            }

            // collector arrives late for some reason
            yield()

            val result = atomic(0)
            us.collect { result.incrementAndGet() }
            result.value shouldBeEqualTo emitSize

            // 이미 collector가 등록되었으므로, 예외가 발생한다
            assertFailsWith<IllegalStateException> {
                us.collect {
                    log.trace { "Collecting2 $it" }
                }
            }
        }
    }
}
