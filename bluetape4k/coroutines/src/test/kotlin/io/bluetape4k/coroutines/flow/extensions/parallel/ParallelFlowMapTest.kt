package io.bluetape4k.coroutines.flow.extensions.parallel

import io.bluetape4k.coroutines.flow.extensions.range
import io.bluetape4k.coroutines.tests.assertError
import io.bluetape4k.coroutines.tests.assertFailure
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.coroutines.tests.withParallels
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.flow.flowOf
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class ParallelFlowMapTest {

    companion object: KLogging()

    @Test
    fun map() = runSuspendTest {
        withParallels(1) { execs ->
            execs shouldHaveSize 1

            range(1, 5)
                .parallel(execs.size) { execs[it] }
                .map {
                    log.trace { "emit $it" }
                    it + 1
                }
                .sequential()
                .assertResult(2, 3, 4, 5, 6)
        }
    }

    @Test
    fun map2() = runSuspendTest {
        withParallels(2) { execs ->
            range(1, 5)
                .parallel(execs.size) { execs[it] }
                .map {
                    log.trace { "emit $it" }
                    it + 1
                }
                .sequential()
                .assertResultSet(2, 3, 4, 5, 6)
        }
    }

    @Test
    fun mapError() = runSuspendTest {
        withParallels(1) { execs ->

            flowOf(1, 0)
                .parallel(execs.size) { execs[it] }
                .map {
                    log.trace { "emit $it" }
                    1 / it
                }
                .sequential()
                .assertFailure<Int, ArithmeticException>(1)
//                .test {
//                    awaitItem() shouldBeEqualTo 1
//                    awaitError() shouldBeInstanceOf ArithmeticException::class
//                }
        }
    }

    @Test
    fun mapError2() = runSuspendTest {
        withParallels(2) { execs ->
            flowOf(1, 2, 0, 3, 4, 0)
                .parallel(execs.size) { execs[it] }
                .map {
                    log.trace { "emit $it" }
                    1 / it
                }
                .sequential()
                .assertError<ArithmeticException>()
        }
    }
}
