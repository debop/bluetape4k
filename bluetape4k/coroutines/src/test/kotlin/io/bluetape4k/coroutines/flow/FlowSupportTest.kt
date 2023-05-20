package io.bluetape4k.coroutines.flow

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
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

class FlowSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
    }

    private val dispatcher = newFixedThreadPoolContext(8, "flowext")

    @RepeatedTest(REPEAT_SIZE)
    fun `repeatFlow operator`() = runSuspendTest {
        val repeated = repeatFlow(4) {
            log.trace { "Processing $it" }
            delay(Random.nextLong(30))
            42
        }
            .flowOn(dispatcher)
            .toFastList()

        repeated.size shouldBeEqualTo 4
        repeated.toUnifiedSet() shouldBeEqualTo setOf(42)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `map async with dispatcher`() = runTest {
        val ranges = (1..20)
        val results = ranges.asFlow()
            .asyncMap(dispatcher) {
                log.trace { "AsyncMap Started $it" }
                delay(Random.nextLong(30))
                it
            }
            .map {
                log.trace { "Map Completed $it" }
                it
            }.toFastList()

        results shouldContainSame ranges
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `chunk flow`() = runTest {
        val _chunkCount = atomic(0)
        val chunkCount by _chunkCount
        val chunkSize = 5

        (1..20).asFlow()
            .chunked(chunkSize)
            .collect { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeEqualTo chunkSize
                _chunkCount.incrementAndGet()
            }

        chunkCount shouldBeEqualTo 4
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `chunk flow with remaining`() = runTest {
        val _chunkCount = atomic(0)
        val chunkCount by _chunkCount
        val chunkSize = 3

        (1..20).asFlow()
            .chunked(chunkSize)
            .onEach { chunked ->
                log.trace { "chunked=$chunked" }
                chunked.size shouldBeLessOrEqualTo chunkSize
                _chunkCount.incrementAndGet()
            }
            .collect()
        chunkCount shouldBeEqualTo 7
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `sliding flow`() = runTest {
        val _slidingCount = atomic(0)
        val slidingCount by _slidingCount
        val slidingSize = 5

        (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                _slidingCount.incrementAndGet()
            }
            .collect()

        slidingCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `sliding flow with remaining`() = runTest {
        val _slidingCount = atomic(0)
        val slidingCount by _slidingCount
        val slidingSize = 3

        (1..20).asFlow()
            .sliding(slidingSize)
            .onEach { slide ->
                log.trace { "slide=$slide" }
                slide.size shouldBeLessOrEqualTo slidingSize
                _slidingCount.incrementAndGet()
            }
            .collect()

        slidingCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow`() = runTest {
        val _windowedCount = atomic(0)
        val windowedCount by _windowedCount
        val windowedSize = 5
        val windowedStep = 1

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeLessOrEqualTo windowedSize
                _windowedCount.incrementAndGet()
            }
            .collect()

        windowedCount shouldBeEqualTo 20
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow with remaining`() = runTest {
        val _windowedCount = atomic(0)
        val windowedCount by _windowedCount
        val windowedSize = 5
        val windowedStep = 4

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeLessOrEqualTo windowedSize
                _windowedCount.incrementAndGet()
            }
            .collect()

        windowedCount shouldBeEqualTo 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `windowed flow no duplicated`() = runTest {
        val _windowedCount = atomic(0)
        val windowedCount by _windowedCount
        val windowedSize = 5
        val windowedStep = 5

        (1..20).asFlow()
            .windowed(windowedSize, windowedStep)
            .onEach { windowed ->
                log.trace { "windowed=$windowed" }
                windowed.size shouldBeEqualTo windowedSize
                _windowedCount.incrementAndGet()
            }
            .collect()

        windowedCount shouldBeEqualTo 4
    }

    @Nested
    inner class Windowed2 {
        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow`() = runTest {
            val _windowedCount = atomic(0)
            val windowedCount by _windowedCount
            val windowedSize = 5
            val windowedStep = 1

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    _windowedCount.incrementAndGet()
                }
                .collect()

            windowedCount shouldBeEqualTo 20
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow with remaining`() = runTest {
            val _windowedCount = atomic(0)
            val windowedCount by _windowedCount
            val windowedSize = 5
            val windowedStep = 4

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeLessOrEqualTo windowedSize
                    _windowedCount.incrementAndGet()
                }
                .collect()

            windowedCount shouldBeEqualTo 5
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `windowed flow no duplicated`() = runTest {
            val _windowedCount = atomic(0)
            val windowedCount by _windowedCount
            val windowedSize = 5
            val windowedStep = 5

            (1..20).asFlow()
                .windowed2(windowedSize, windowedStep)
                .onEach { windowed ->
                    val items = windowed.toList()
                    log.trace { "windowed items=$items" }
                    items.size shouldBeEqualTo windowedSize
                    _windowedCount.incrementAndGet()
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
                .toFastList()

            orders shouldHaveSize orderCount
            orders.all { it.items.size == itemCount }.shouldBeTrue()
            orders.forEach { order ->
                log.trace { "order=$order" }
            }
        }

        private fun getOrderRows(orderCount: Int = 4, itemCount: Int = 5): Flow<OrderRow> {
            log.trace { "order=$orderCount, item=$itemCount" }
            return fastList(orderCount) { oid ->
                fastList(itemCount) { itemId ->
                    OrderRow(
                        oid + 1,
                        (oid + 1) * 10 + itemId + 1,
                        Fakers.fixedString(16),
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
