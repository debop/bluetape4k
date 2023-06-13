package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class MergeFlowsTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `merge flows`() = runTest {
        mergeFlows(
            range(6, 5).log(6),
            range(1, 5).log(1),
        )
            .assertResultSet(6, 7, 8, 9, 10, 1, 2, 3, 4, 5)
    }

    @Test
    fun `one source`() = runTest {
        mergeFlows(range(1, 5))
            .assertResultSet(1, 2, 3, 4, 5)
    }

    @Test
    fun `no source`() = runTest {
        emptyList<Flow<Int>>()
            .mergeFlows()
            .assertResult()
    }

    @Test
    fun `many async`() = runTest {
        val n = 1_000

        mergeFlows(
            range(0, n / 2).startCollectOn(Dispatchers.Default).log(1),
            range(0, n / 2).startCollectOn(Dispatchers.Unconfined).log(2),
        )
            .count() shouldBeEqualTo n
    }
}
