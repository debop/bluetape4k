package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.junit.jupiter.api.Test

class ChunkedTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `chunk flow`() = runTest {
        val chunkCounter = atomic(0)
        val chunkCount by chunkCounter
        val chunkSize = 5

        val chunks = (1..20).asFlow()
            .chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeEqualTo chunkSize
                chunkCounter.incrementAndGet()
            }
            .toList()

        chunkCount shouldBeEqualTo 4
        chunks.size shouldBeEqualTo 4
        chunks.last() shouldBeEqualTo listOf(16, 17, 18, 19, 20)
    }

    @Test
    fun `chunk flow with remaining`() = runTest {
        val chunkCounter = atomic(0)
        val chunkCount by chunkCounter
        val chunkSize = 3

        val chunks = (1..20).asFlow()
            .chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeLessOrEqualTo chunkSize
                chunkCounter.incrementAndGet()
            }
            .toList()

        chunkCount shouldBeEqualTo 7
        chunks.size shouldBeEqualTo 7
        chunks.last() shouldBeEqualTo listOf(19, 20)
    }
}
