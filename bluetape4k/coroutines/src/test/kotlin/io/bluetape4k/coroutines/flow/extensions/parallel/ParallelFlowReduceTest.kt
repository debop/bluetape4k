package io.bluetape4k.coroutines.flow.extensions.parallel

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.withParallels
import io.bluetape4k.junit5.coroutines.runSuspendTest
import kotlinx.coroutines.flow.asFlow
import org.junit.jupiter.api.Test

class ParallelFlowReduceTest {

    @Test
    fun basic() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf(1, 2, 3, 4, 5)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .reduce({ 0 }, { a, b -> a + b })
                .sequential()
                .assertResult(15)
        }
    }

    @Test
    fun reduceSeq() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf(1, 2, 3, 4, 5)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .reduce { a, b -> a + b }
                .assertResult(15)
        }
    }

    @Test
    fun reduceSeqEmpty() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf<Int>()
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .reduce({ 0 }) { a, b -> a + b }
                .sequential()
                .assertResult(0)
        }
    }

    @Test
    fun reduceSeqEmpy() = runSuspendTest {
        withParallels(1) { execs ->
            arrayOf<Int>()
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .reduce { a, b -> a + b }
                .assertResult()
        }
    }

    @Test
    fun reduceSeq2() = runSuspendTest {
        withParallels(2) { execs ->
            arrayOf(1, 2, 3, 4, 5)
                .asFlow()
                .parallel(execs.size) { execs[it] }
                .reduce { a, b -> a + b }
                .assertResult(15)
        }
    }
}
