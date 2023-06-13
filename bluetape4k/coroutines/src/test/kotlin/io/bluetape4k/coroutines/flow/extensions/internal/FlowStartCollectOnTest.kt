package io.bluetape4k.coroutines.flow.extensions.internal

import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.coroutines.flow.extensions.range
import io.bluetape4k.coroutines.flow.extensions.startCollectOn
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@Deprecated("move to StartCollectOnTest")
class FlowStartCollectOnTest {

    companion object: KLogging()

    @Test
    fun `use start collect on with custom dispatcher`() = runTest {
        val four = newFixedThreadPoolContext(4, "four")
        val single = newSingleThreadContext("single")

        range(1, 10)
            .log("startCollectOn")
            .startCollectOn(four)
            .flowOn(single)
            .log("flowOn")
            .assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        four.close()
        single.close()
    }
}
