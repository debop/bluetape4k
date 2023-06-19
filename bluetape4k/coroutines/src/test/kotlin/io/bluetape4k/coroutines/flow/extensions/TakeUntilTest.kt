package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.coroutines.tests.assertError
import io.bluetape4k.coroutines.tests.assertResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.time.Duration

class TakeUntilTest: AbstractFlowTest() {

    @Test
    fun basic() = runTest {
        range(1, 10).log("source")
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(550)).log("takeUntil")
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `takeUntil has longer timeout`() = runTest {
        range(1, 10).log("source")
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(1500)).log("takeUntil")
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    @Test
    fun `takeUntil with single value flow`() = runTest {
        range(1, 5).log("source")
            .takeUntil(flowOf(1)).log("takeUntil")
            .assertResult()
    }

    @Test
    fun `source flow is completed after untifier start`() = runTest {
        //-----1-----2-----3-----4-----5
        //--------------------|
        range(1, 5).log("source")
            .onEach { delay(100) }
            .takeUntil(delayedFlow(350)).log("takeUntil")
            .assertResult(1, 2, 3)
    }

    @Test
    fun `source flow is completed before untifier start`() = runTest {
        //-----1-----2-----3-----4-----5
        //----------------------------------|
        range(1, 5).log("source")
            .onEach { delay(100) }
            .takeUntil(delayedFlow(6000)).log("takeUntil")
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `source flow is complete after duration`() = runTest {
        //-----1-----2-----3-----4-----5
        //--------------------|
        range(1, 5).log("source")
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(350)).log("takeUntil")
            .assertResult(1, 2, 3)
    }

    @Test
    fun `source flow is complete before duration`() = runTest {
        //-----1-----2-----3-----4-----5
        //----------------------------------|
        range(1, 5).log("source")
            .onEach { delay(100) }
            .takeUntil(Duration.ofMillis(600)).log("takeUntil")
            .assertResult(1, 2, 3, 4, 5)
    }

    @Test
    fun `when upstream failure`() = runTest {
        flow<Nothing> { throw RuntimeException("Boom!") }.log("source")
            .takeUntil(delayedFlow(100)).log("takeUntil")
            .assertError<RuntimeException>()

        flow {
            emit(1)
            throw RuntimeException("Boom!")
        }
            .log("source")
            .takeUntil(delayedFlow(100)).log("takeUntil")
            .test {
                awaitItem() shouldBeEqualTo 1
                awaitError() shouldBeInstanceOf RuntimeException::class
            }
    }

    @Test
    fun `take before notification`() = runTest {
        flowOf(1, 2, 3).log("source")
            .takeUntil(delayedFlow(100)).log("takeUntil")
            .take(1)
            .assertResult(1)
    }
}