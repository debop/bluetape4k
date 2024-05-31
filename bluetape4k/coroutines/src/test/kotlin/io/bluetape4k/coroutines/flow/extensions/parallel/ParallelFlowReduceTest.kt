package io.bluetape4k.coroutines.flow.extensions.parallel

import io.bluetape4k.coroutines.flow.extensions.flowRangeOf
import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.coroutines.tests.withParallels
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class ParallelFlowReduceTest {

    companion object: KLogging()

    @Test
    fun basic() = runTest {
        withParallels(1) { execs ->
            execs shouldHaveSize 1
            flowRangeOf(1, 5).log("source")
                .parallel(execs.size) { execs[it] }
                .reduce({ 0 }, { a, b -> a + b })
                .sequential().log("sequential")
                .assertResult(15)
        }
    }

    @Test
    fun reduceSeq() = runTest {
        withParallels(1) { execs ->
            flowRangeOf(1, 5)
                .parallel(execs.size) { execs[it] }
                .reduce { a, b ->
                    log.trace { "a=$a, b=$b" }
                    a + b
                }
                .assertResult(15)
        }
    }

    @Test
    fun reduceSeqEmpty() = runSuspendTest {
        withParallels(1) { execs ->
            emptyFlow<Int>()
                .parallel(execs.size) { execs[it] }
                .reduce({ 0 }) { a, b ->
                    log.trace { "a=$a, b=$b" }
                    a + b
                }
                .sequential()
                .assertResult(0)
        }
    }

    @Test
    fun reduceSeqEmpy() = runSuspendTest {
        withParallels(1) { execs ->
            emptyFlow<Int>()
                .parallel(execs.size) { execs[it] }
                .reduce { a, b -> a + b }
                .assertResult()
        }
    }

    @Test
    fun reduceSeq2() = runSuspendTest {
        withParallels(2) { execs ->
            execs shouldHaveSize 2

            flowRangeOf(1, 5).log("source")
                .parallel(execs.size) { execs[it] }
                .reduce { a, b ->
                    log.trace { "a=$a, b=$b" }
                    a + b
                }
                .log("reduce")
                .assertResult(15)
        }
    }
}
