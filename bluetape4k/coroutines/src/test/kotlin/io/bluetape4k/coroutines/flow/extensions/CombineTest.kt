package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class CombineTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `combine 4 flows`() = runTest(StandardTestDispatcher()) {
        val list = combine(
            flow1(),
            flow2(),
            flow3(),
            flow4(),
        ) { f1, f2, f3, f4 ->
            listOf(f1, f2, f3, f4).joinToString("-")
        }.toList()

        list shouldBeEqualTo listOf(
            "1-a-true-a",
            "2-b-false-b",
            "3-c-true-c",
        )
    }

    private fun flow1(): Flow<Int> = flowOf(1, 2, 3)
    private fun flow2(): Flow<String> = flowOf("a", "b", "c")
    private fun flow3(): Flow<Boolean> = flowOf(true, false, true)
    private fun flow4(): Flow<Char> = flowOf('a', 'b', 'c')

}
