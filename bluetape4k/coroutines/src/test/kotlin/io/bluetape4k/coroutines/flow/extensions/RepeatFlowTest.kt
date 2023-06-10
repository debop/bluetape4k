package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.newFixedThreadPoolContext
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class RepeatFlowTest: AbstractFlowTest() {

    companion object: KLogging()

    private val dispatcher = newFixedThreadPoolContext(8, "flowext")

    @RepeatedTest(REPEAT_SIZE)
    fun `repeatFlow operator`() = runSuspendTest {
        val repeated = repeatFlow(4) {
            log.trace { "Processing $it" }
            delay(Random.nextLong(5))
            42
        }
            .flowOn(dispatcher)
            .toFastList()

        repeated.size shouldBeEqualTo 4
        repeated.distinct() shouldBeEqualTo listOf(42)
    }
}
