package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.flow.extensions.mergeFlows
import io.bluetape4k.coroutines.flow.extensions.startCollectOn
import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlowMergeFlowsTest {

    companion object: KLogging()

    @Test
    fun `merge flows`() = runTest {
        mergeFlows(
            flowOfRange(6, 5),
            flowOfRange(1, 5),
        )
            .assertResultSet(6, 7, 8, 9, 10, 1, 2, 3, 4, 5)
    }

    @Test
    fun `one source`() = runTest {
        mergeFlows(flowOfRange(1, 5))
            .assertResultSet(1, 2, 3, 4, 5)
    }

    @Test
    fun `no source`() = runTest {
        emptyList<Flow<Int>>()
            .mergeFlows()
            .assertResultSet()
    }

    @Test
    fun `many async`() = runTest {
        val n = 10_000

        val m = mergeFlows(
            flowOfRange(0, n / 2).startCollectOn(Dispatchers.IO),
            flowOfRange(0, n / 2).startCollectOn(Dispatchers.IO),
        )
            .count()

        m shouldBeEqualTo n
    }
}
