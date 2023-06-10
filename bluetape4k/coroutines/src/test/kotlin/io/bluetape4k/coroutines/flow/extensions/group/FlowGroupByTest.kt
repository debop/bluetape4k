package io.bluetape4k.coroutines.flow.extensions.group

import io.bluetape4k.coroutines.flow.eclipse.toMultiMap
import io.bluetape4k.coroutines.flow.extensions.asFlow
import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FlowGroupByTest {

    companion object: KLogging()

    @Test
    fun `group by with key`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 2 }
            .flatMapMerge { group -> group.toValues() }
            .assertResultSet(listOf(1, 3, 5, 7, 9), listOf(2, 4, 6, 8, 10))
    }

    @Test
    fun `group by with key and values`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 2 }
            .flatMapMerge { group -> group.toGroupItem() }
            .onEach { log.debug { "grouped item=$it" } }
            .assertResultSet(
                GroupItem(1, listOf(1, 3, 5, 7, 9)),
                GroupItem(0, listOf(2, 4, 6, 8, 10)),
            )
    }

    @Test
    fun `group by with value selector`() = runTest {
        flowOfRange(1, 10)
            .groupBy({ it % 2 }) { it + 1 }
            .flatMapMerge { it.toValues() }
            .assertResultSet(listOf(2, 4, 6, 8, 10), listOf(3, 5, 7, 9, 11))
    }

    @Test
    fun `one of each`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 2 }
            .flatMapMerge {
                it.take(1).onEach { item ->
                    log.debug { "grouped item=$item" }
                }
            }
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    @Test
    fun `max groups`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 3 }
            .flatMapMerge { it.toValues() }
            .assertResultSet(listOf(1, 4, 7, 10), listOf(2, 5, 8), listOf(3, 6, 9))

        flowOfRange(1, 10)
            .groupBy { it % 3 }
            .take(2)                    // list(3, 6, 9) 는 빠진다
            .flatMapMerge { it.toValues() }
            .assertResultSet(listOf(1, 4, 7, 10), listOf(2, 5, 8))
    }

    @Test
    fun `take items`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 2 }
            .flatMapMerge { it }
            .take(3)
            .onEach { log.trace { "item=$it" } }
            .assertResult(1, 2, 3)
    }

    @Test
    fun `take groups and items`() = runTest {
        flowOfRange(1, 10)
            .groupBy { it % 3 }
            .take(2)
            .flatMapMerge { it }
            .take(2)
            .assertResult(1, 2)
    }

    @Test
    fun `main errors no items`() = runTest {
        assertFailsWith<IllegalStateException> {
            (1..10).asFlow()
                .map { if (it < 5) error("oops") else it }
                .groupBy { it % 2 == 0 }
                .flatMapMerge { it }
                .onEach { log.trace { it } }
                .collect()
        }
    }

    @Test
    fun `main errors some items`() = runTest {
        assertFailsWith<IllegalStateException> {
            (1..10).asFlow()
                .map { if (it > 5) error("oops") else it }
                .groupBy { it % 2 == 0 }
                .flatMapMerge { it }
                .onEach { log.trace { it } }
                .collect()
        }
    }

    @Test
    fun `convert Group to Map`() = runTest {
        val map = flowOfRange(1, 10)
            .groupBy { it % 2 }
            .toMap()
        map shouldBeEqualTo mapOf(0 to listOf(2, 4, 6, 8, 10), 1 to listOf(1, 3, 5, 7, 9))
    }

    @Test
    fun `convert Group to Multimap`() = runTest {
        val mmap = flowOfRange(1, 10)
            .groupBy { it % 2 }
            .toMultiMap()

        mmap.size() shouldBeEqualTo 10
        mmap.keySet() shouldHaveSize 2
        mmap[0] shouldBeEqualTo listOf(2, 4, 6, 8, 10)
        mmap[1] shouldBeEqualTo listOf(1, 3, 5, 7, 9)
    }
}
