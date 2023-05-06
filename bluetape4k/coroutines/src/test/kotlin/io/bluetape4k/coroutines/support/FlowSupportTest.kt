package io.bluetape4k.coroutines.support

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.support.randomString
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

@OptIn(DelicateCoroutinesApi::class)
class FlowSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    private val dispatcher = newFixedThreadPoolContext(4, "flowext")

    @RepeatedTest(REPEAT_SIZE)
    fun `repeatFlow operator`() = runTest {
        val repeated = repeatFlow(4) {
            log.debug { "Processing $it" }
            delay(Random.nextLong(100))
            42
        }
            .flowOn(dispatcher)
            .toList()

        repeated.size shouldBeEqualTo 4
        repeated.toSet() shouldBeEqualTo setOf(42)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `map async with dispatcher`() = runTest {
        val ranges = (1..20)
        val results = ranges.asFlow()
            .asyncMap(dispatcher) {
                log.trace { "AsyncMap Started $it" }
                delay(Random.nextLong(300))
                it
            }
            .map {
                log.trace { "Map Completed $it" }
                it
            }.toList()

        results shouldContainSame ranges
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `chunk flow`() = runTest {
        val chunkCounter = atomic(0)
        val chunkCount by chunkCounter
        val chunkSize = 5

        (1..20).asFlow()
            .chunked(chunkSize)
            .collect { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeEqualTo chunkSize
                chunkCounter.incrementAndGet()
            }

        chunkCount shouldBeEqualTo 4
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `chunk flow with remaining`() = runTest {
        val chunkCounter = atomic(0)
        val chunkCount by chunkCounter
        val chunkSize = 3

        (1..20).asFlow()
            .chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeLessOrEqualTo chunkSize
                chunkCounter.incrementAndGet()
            }
            .collect()
        chunkCount shouldBeEqualTo 7
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `sliding flow`() = runTest {
        val slidingCounter = atomic(0)
        val slidingCount by slidingCounter
        val slidingSize = 5

        (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                slidingCounter.incrementAndGet()
            }
            .collect()
        slidingCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `sliding flow with remaining`() = runTest {
        val slidingCounter = atomic(0)
        val slidingCount by slidingCounter
        val slidingSize = 3

        (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                slidingCounter.incrementAndGet()
            }
            .collect()
        slidingCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow`() = runTest {
        val windowedCounter = atomic(0)
        val windowedCount by windowedCounter
        val windowedSize = 5
        val windowedStep = 1

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeLessOrEqualTo windowedSize
                windowedCounter.incrementAndGet()
            }
            .collect()
        windowedCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow with remaining`() = runTest {
        val windowedCounter = atomic(0)
        val windowedCount by windowedCounter
        val windowedSize = 5
        val windowedStep = 4

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeLessOrEqualTo windowedSize
                windowedCounter.incrementAndGet()
            }
            .collect()
        windowedCount shouldBeEqualTo 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow no duplicated`() = runTest {
        val windowedCounter = atomic(0)
        val windowedCount by windowedCounter
        val windowedSize = 5
        val windowedStep = 5

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeEqualTo windowedSize
                windowedCounter.incrementAndGet()
            }
            .collect()
        windowedCount shouldBeEqualTo 4
    }

    @Nested
    inner class Windowed2 {
        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow`() = runTest {
            val windowedCounter = atomic(0)
            val windowedCount by windowedCounter
            val windowedSize = 5
            val windowedStep = 1

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .collect()
            windowedCount shouldBeEqualTo 20
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow with remaining`() = runTest {
            val windowedCounter = atomic(0)
            val windowedCount by windowedCounter
            val windowedSize = 5
            val windowedStep = 4

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .collect()
            windowedCount shouldBeEqualTo 5
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow no duplicated`() = runTest {
            val windowedCounter = atomic(0)
            val windowedCount by windowedCounter
            val windowedSize = 5
            val windowedStep = 5

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeEqualTo windowedSize
                    windowedCounter.incrementAndGet()
                }
                .collect()
            windowedCount shouldBeEqualTo 4
        }
    }

    @Nested
    inner class BufferUntilChanged {

        @RepeatedTest(REPEAT_SIZE)
        fun `master id 가 같은 경우끼리 하나의 List로 묶습니다`() = runTest {
            val orderCount = Random.nextInt(3, 6)
            val itemCount = Random.nextInt(2, 9)
            val orders = getOrderRows(orderCount, itemCount)
                .bufferUntilChanged { it.orderId }
                .map { rows ->
                    Order(rows[0].orderId, rows.map { OrderItem(it.itemId, it.itemName, it.itemQuantity) })
                }
                .toList()

            orders shouldHaveSize orderCount
            orders.all { it.items.size == itemCount }.shouldBeTrue()
            orders.forEach { order ->
                log.trace { "order=$order" }
            }
        }

        private fun getOrderRows(orderCount: Int = 4, itemCount: Int = 5): Flow<OrderRow> {
            log.debug { "order=$orderCount, item=$itemCount" }
            return List(orderCount) { oid ->
                List(itemCount) { itemId ->
                    OrderRow(
                        oid + 1,
                        (oid + 1) * 10 + itemId + 1,
                        randomString(16),
                        Random.nextInt(10, 99)
                    )
                }
            }.flatten().asFlow()
        }
    }

    data class OrderRow(
        val orderId: Int,
        val itemId: Int,
        val itemName: String,
        val itemQuantity: Int,
    )

    data class Order(
        val id: Int,
        val items: List<OrderItem>,
    )

    data class OrderItem(
        val id: Int,
        val name: String,
        val quantity: Int,
    )
}
