package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class StartCollectOnTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `use start collect on with custom dispatcher`() = runTest {
        val four = newFixedThreadPoolContext(4, "four")
        val single = newSingleThreadContext("single")

        range(1, 10)
            .onEach { log.trace { "startCollectOn $it" } }
            .startCollectOn(four)
            .onEach { log.trace { "flowOn $it" } }
            .flowOn(single)
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        four.close()
        single.close()
    }
}
