package io.bluetape4k.collections.eclipse.parallel

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldNotBeEqualTo
import org.eclipse.collections.impl.list.mutable.FastList
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class ParallelSupportTest {

    companion object: KLogging() {
        private const val COUNT = DEFAULT_BATCH_SIZE * 16
        private val intRange = (0 until COUNT)

        private const val LIST_COUNT = 10_000
    }

    private val xs: FastList<Int> = fastList(COUNT) { it }
    private val xss = (0..8).chunked(3).toFastList()

    @Test
    fun `parallel filter`() {
        val even = xs.parFilter { it % 2 == 0 }
        val odd = xs.parFilter { it % 2 == 1 }

        even.size shouldBeEqualTo COUNT / 2
        odd.size shouldBeEqualTo COUNT / 2
    }


    @Test
    fun `parallel reject`() {
        val odd = xs.parReject { it % 2 == 0 }
        val even = xs.parReject { it % 2 == 1 }

        odd.size shouldBeEqualTo COUNT / 2
        even.size shouldBeEqualTo COUNT / 2
    }

    @Test
    fun `parallel count`() {
        val count = LIST_COUNT
        val xs = fastList(count) { it }

        val fastTime = measureNanoTime {
            xs.parCount(count / 100) {
                // Thread.sleep(1)
                it % 2 == 0
            } shouldBeEqualTo count / 2
        }

        val slowTime = measureNanoTime {
            xs.parCount(1) {
                // Thread.sleep(1)
                it % 2 == 0
            } shouldBeEqualTo count / 2
        }

        log.debug { "fastTime=$fastTime, slowTime=$slowTime" }
        fastTime shouldBeLessThan slowTime
    }

    @Test
    fun `paralle forEach`() {
        val even = ConcurrentLinkedQueue<Int>()

        xs.parForEach(COUNT / 10) {
            if (it % 2 == 0) {
                even.add(it)
            }
        }
        even.size shouldBeEqualTo COUNT / 2
        even shouldNotBeEqualTo even.sorted()
    }

    @Test
    fun `parallel forEach with index`() {
        val even = ConcurrentLinkedQueue<Int>()

        xs.parForEachWithIndex(COUNT / 2) { _, elem ->
            if (elem % 2 == 0) {
                even.add(elem)
            }
        }
        even.size shouldBeEqualTo COUNT / 2
        even shouldNotBeEqualTo even.sorted()
    }

    @Test
    fun `parallel map`() {
        val times = xs.parMap(3, reorder = false) {
            // log.trace { "parMap=$it" }
            it * 2

        }
        times.size shouldBeEqualTo COUNT
        // times.take(7) shouldNotBeEqualTo listOf(0, 2, 4, 6, 8, 10, 12)
        times.sorted().take(7) shouldBeEqualTo listOf(0, 2, 4, 6, 8, 10, 12)
    }

    @Test
    fun `parallel flat map`() {
        val fm = xs.parFlatMap(COUNT / 100) { x ->
            List(3) { x.toLong() + it }
        }

        fm.size shouldBeEqualTo COUNT * 3
        fm shouldNotBeEqualTo fm.sorted()
    }

    @Test
    fun `parallel filter map`() {
        val random = Random(System.currentTimeMillis())

        val fm = xs.take(COUNT / 100)
            .parFilterMap(
                reorder = false,
                predicate = { it % 2 == 0 },
                mapper = { Thread.sleep(random.nextLong(2L)); it * 2 })
            .toList()
        fm.size shouldBeEqualTo COUNT / 200
    }

    @Test
    fun `parallel group by`() {
        val groupBy = xs.parGroupBy(COUNT / 10) { it % 4 }

        groupBy.keysView().size() shouldBeEqualTo 4
        groupBy[0].size shouldBeEqualTo COUNT / 4
        groupBy[1].size shouldBeEqualTo COUNT / 4
        groupBy[2].size shouldBeEqualTo COUNT / 4
        groupBy[3].size shouldBeEqualTo COUNT / 4
    }

    @Test
    fun `parallel aggregate by`() {
        val agg = xs.parAggregateBy(
            COUNT / 10,
            groupBy = { 1 },
            zeroValueFactory = { 0 },
            nonMutatingAggregator = { acc: Long, item -> acc + item + 1 }
        )

        agg.keys.size shouldBeEqualTo 1
        agg[1] shouldBeEqualTo 12800080000L
    }

    @Test
    fun `parallel aggregate by 10`() {
        val agg = xs.parAggregateBy(
            COUNT / 10,
            groupBy = { it % 10 },
            zeroValueFactory = { 0 },
            nonMutatingAggregator = { acc: Long, item -> acc + item + 1 }
        )

        agg.keys.size shouldBeEqualTo 10

        agg.keys.forEach { i ->
            log.trace { "agg[$i]=${agg[i]}" }
            agg[i]!! shouldBeEqualTo (1279936000L + 16000L * i)
        }
        // agg[1] shouldBeEqualTo 12800080000L
    }

    @Test
    fun `benchmark array with java parallelStream`() {
        val suffix = "value"
        val xs = fastList(COUNT) { "$suffix-$it" }

        val mapper: (String) -> Int = { it.drop(suffix.length + 1).toInt() }
        val batchSize = COUNT / Runtime.getRuntime().availableProcessors() / 2

        repeat(1) {
            xs.parallelStream().map(mapper)
            xs.parMap { mapper.invoke(it) }
        }

        val parallel = measureTimeMillis {
            repeat(5) {
                xs.parallelStream().map(mapper).toList()
            }
        }

        val parMap = measureTimeMillis {
            repeat(5) {
                xs.parMap(batchSize = batchSize) { mapper(it) }
            }
        }

        log.debug { "parallel=$parallel ms, parMap=$parMap ms" }
    }
}
