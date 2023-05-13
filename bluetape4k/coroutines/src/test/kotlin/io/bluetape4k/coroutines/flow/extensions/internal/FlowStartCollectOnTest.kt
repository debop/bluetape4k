package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.flowOfRange
import io.bluetape4k.coroutines.flow.extensions.startCollectOn
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@Suppress("OPT_IN_USAGE")
class FlowStartCollectOnTest {

    companion object: KLogging()

    @Test
    fun `use start collect on with custom dispatcher`() = runTest {
        val four = newFixedThreadPoolContext(4, "four")
        val single = newSingleThreadContext("single")

        flowOfRange(1, 10)
            .onEach { log.debug { "startCollectOn $it" } }
            .startCollectOn(four)
            .onEach { log.debug { "flowOn $it" } }
            .flowOn(single)
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        four.close()
        single.close()
    }
}
