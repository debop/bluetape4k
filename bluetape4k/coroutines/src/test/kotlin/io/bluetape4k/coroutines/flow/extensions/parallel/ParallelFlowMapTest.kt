package io.bluetape4k.coroutines.flow.extensions.parallel

import io.bluetape4k.coroutines.tests.assertError
import io.bluetape4k.coroutines.tests.assertFailure
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.assertResultSet
import io.bluetape4k.coroutines.tests.withParallels
import io.bluetape4k.junit5.coroutines.runSuspendTest
import kotlinx.coroutines.flow.asFlow
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ParallelFlowMapTest {
    @Test
    fun map() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf(1, 2, 3, 4, 5)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .map { it + 1 }
                .sequential()
                .assertResult(2, 3, 4, 5, 6)
        }
    }

    @Test
    fun map2() = runSuspendTest {
        withParallels(2) { execs ->
            arrayOf(1, 2, 3, 4, 5)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .map { it + 1 }
                .sequential()
                .assertResultSet(2, 3, 4, 5, 6)
        }
    }

    @Test
    fun mapError() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf(1, 0)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .map { 1 / it }
                .sequential()
                .assertFailure<Int, ArithmeticException>(1)
        }
    }

    @Test
    @Disabled("Parallel exceptions are still a mystery")
    fun mapError2() = runSuspendTest {
        withParallels(2) { execs ->
            arrayOf(1, 2, 0, 3, 4, 0)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .map { 1 / it }
                .sequential()
                .assertError<ArithmeticException>()
        }
    }
}
