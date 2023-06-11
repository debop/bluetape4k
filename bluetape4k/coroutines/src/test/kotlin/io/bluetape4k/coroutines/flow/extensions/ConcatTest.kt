package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ConcatTest: AbstractFlowTest() {

    companion object: KLogging()

    val flow1 = flowOf(1, 2)
    val flow2 = flowOf(3, 4)
    val flow3 = flowOf(5, 6)

    @Test
    fun `concat multiple flows`() = runTest {
        val concat = concat(flow1, flow2).toList()
        concat shouldBeEqualTo listOf(1, 2, 3, 4)
    }

    @Test
    fun `concat with 3 flows`() = runTest {
        val concat = concat(flow1, flow2, flow3).toList()
        concat shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `concat with`() = runTest {
        val concat1 = flow1.concatWith(flow2).toList()
        concat1 shouldBeEqualTo listOf(1, 2, 3, 4)

        val concat2 = flow1.concatWith(flow2, flow3).toList()
        concat2 shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `concat collection of flows`() = runTest {
        val concat = listOf(flow1, flow2, flow3).concat().toList()
        concat shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `startWith items`() = runTest {
        val list = flow1.startWith(0).toList()
        list shouldBeEqualTo listOf(0, 1, 2)

        val list2 = flow1.startWith(-2, -1, 0).toList()
        list2 shouldBeEqualTo listOf(-2, -1, 0, 1, 2)
    }

    @Test
    fun `startWith flows`() = runTest {
        val list = flow3.startWith(flow1, flow2).toList()
        list shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `endWith items`() = runTest {
        val list = flow1.endWith(3).toList()
        list shouldBeEqualTo listOf(1, 2, 3)

        val list2 = flow1.endWith(3, 4).toList()
        list2 shouldBeEqualTo listOf(1, 2, 3, 4)
    }

    @Test
    fun `endWith flows`() = runTest {
        val list = flow1.endWith(flow2).toList()
        list shouldBeEqualTo listOf(1, 2, 3, 4)

        val list2 = flow1.endWith(flow2, flow3).toList()
        list2 shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }
}
