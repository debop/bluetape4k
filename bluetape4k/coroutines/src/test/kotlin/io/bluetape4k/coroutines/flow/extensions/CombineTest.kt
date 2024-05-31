package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.tests.assertResult
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CombineTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `combine 4 flows`() = runTest {
        combine(
            flow1(),
            flow2(),
            flow3(),
            flow4(),
        ) { f1, f2, f3, f4 ->
            listOf(f1, f2, f3, f4).joinToString("-")
        }.assertResult(
            "1-a-true-a",
            "2-b-false-b",
            "3-c-true-c",
        )
    }

    private fun flow1(): Flow<Int> = flowOf(1, 2, 3).log("#1")
    private fun flow2(): Flow<String> = flowOf("a", "b", "c").log("#2")
    private fun flow3(): Flow<Boolean> = flowOf(true, false, true).log("#3")
    private fun flow4(): Flow<Char> = flowOf('a', 'b', 'c').log("#4")

}
