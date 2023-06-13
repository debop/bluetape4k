package io.bluetape4k.coroutines.flow.extensions

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ChunkedTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `chunk flow`() = runTest {
        var chunkCount = 0
        val chunkSize = 5

        val chunks = range(1, 20).chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeEqualTo chunkSize
                chunkCount++
            }
            .toList()

        chunkCount shouldBeEqualTo 4
        chunks.size shouldBeEqualTo 4
        chunks.last() shouldBeEqualTo listOf(16, 17, 18, 19, 20)
    }

    @Test
    fun `chunk flow with remaining`() = runTest {
        var chunkCount = 0
        val chunkSize = 3

        val chunks = range(1, 20).chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeLessOrEqualTo chunkSize
                chunkCount++
            }
            .toList()

        chunkCount shouldBeEqualTo 7
        chunks.size shouldBeEqualTo 7
        chunks.last() shouldBeEqualTo listOf(19, 20)
    }

    @Test
    fun `chunked flow - check with turbine`() = runTest {
        range(0, 10)
            .chunked(3)
            .test {
                awaitItem() shouldBeEqualTo listOf(0, 1, 2)
                awaitItem() shouldBeEqualTo listOf(3, 4, 5)
                awaitItem() shouldBeEqualTo listOf(6, 7, 8)
                awaitItem() shouldBeEqualTo listOf(9)
                awaitComplete()
            }
    }

    @Test
    fun `flow 에 예외가 있으면 예외가 발생합니다`() = runTest {
        assertFailsWith<RuntimeException> {
            flow<Int> { throw RuntimeException("Boom!") }
                .chunked(3)
                .collect()
        }
    }

    @Test
    fun `chunked with cancellation`() = runTest {
        range(0, 10)
            .chunked(4)
            .take(2)
            .test {
                awaitItem() shouldBeEqualTo listOf(0, 1, 2, 3)
                awaitItem() shouldBeEqualTo listOf(4, 5, 6, 7)
                awaitComplete()
            }
    }

    @Test
    fun `chunked with mutable shared flow`() = runTest {
        val flow = MutableSharedFlow<Int>(extraBufferCapacity = 64)
        val results = mutableListOf<List<Int>>()

        val job1 = flow.chunked(3)
            .onEach {
                results += it
                if (it == listOf(1, 2, 3)) {
                    flow.tryEmit(4)
                    flow.tryEmit(5)
                    flow.tryEmit(6)
                }
            }.launchIn(this)

        val job2 = launch {
            flow.tryEmit(1)
            flow.tryEmit(2)
            flow.tryEmit(3)
        }

        advanceUntilIdle()
        job1.cancel()
        job2.cancel()

        results shouldBeEqualTo listOf(listOf(1, 2, 3), listOf(4, 5, 6))
    }
}
