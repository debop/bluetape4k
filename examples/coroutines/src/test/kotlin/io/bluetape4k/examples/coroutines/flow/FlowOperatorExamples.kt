package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.examples.coroutines.isEven
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class FlowOperatorExamples {

    companion object: KLogging()

    @Test
    fun `map elements`() = runTest {
        var sum = 0
        flowOf(1, 2, 3)
            .map { it * it }            // [1, 4, 9]
            .collect { sum += it }      // 14

        sum shouldBeEqualTo 1 + 4 + 9
    }

    @Test
    fun `filter elements`() = runTest {
        val evens = flowOf(1, 2, 3, 4)
            .filter { it.isEven() }     // [2, 4]
            .toList()

        evens shouldBeEqualTo listOf(2, 4)
    }

    @Test
    fun `merge element of flows`() = runTest {
        val ints = flowOf(1, 2, 3)
        val doubles = flowOf(0.1, 0.2, 0.3)

        // merge는 복수 개의 flow의 요소들을 합쳐서 하나의 flow로 만든다
        val togather = merge(ints, doubles)
        togather.toFastList() shouldBeEqualTo listOf(1, 2, 3, 0.1, 0.2, 0.3)
    }

    @Test
    fun `merge elements of flows with time`() = runTest {
        val ints = flowOf(1, 2, 3).onEach { delay(100); println("delay 100") }
        val doubles = flowOf(0.1, 0.2, 0.3)

        // merge는 복수 개의 flow의 요소들을 합쳐서 하나의 flow로 만든다
        val togather = merge(ints, doubles)
        togather.toFastList() shouldBeEqualTo listOf(0.1, 0.2, 0.3, 1, 2, 3)
    }

    /**
     * zip 은 각 flow 요소들의 emit 된 순서대로 zip 을 수행합니다.
     */
    @Test
    fun `zip elements of flows`() = runTest {
        val flow1 = flowOf("A", "B", "C").onEach { delay(400); println("delay 400") }
        val flow2 = flowOf(1, 2, 3, 4).onEach { delay(1000); println("delay 1000") }

        val ziped = flow1.zip(flow2) { f1, f2 -> "${f1}_${f2}" }.onEach { println(it) }.toList()

        /**
         * zip 에서는 전송된 요소들의 순서에 맞게 짝을 지운다
         *
         * .... A .... B .... C
         * .............. 1 ............. 2 .......... 3 .......... 4
         */

        /**
         * zip 에서는 전송된 요소들의 순서에 맞게 짝을 지운다
         *
         * .... A .... B .... C
         * .............. 1 ............. 2 .......... 3 .......... 4
         */
        ziped shouldBeEqualTo listOf("A_1", "B_2", "C_3")
    }

    @Test
    fun `combine elements of flows`() = runTest {
        val flow1 = flowOf("A", "B", "C")
            .onEach {
                delay(400)
                println("delay 400, $it")
            }
        val flow2 = flowOf(1, 2, 3, 4)
            .onEach {
                delay(1000)
                println("delay 1000, $it")
            }

        val combined = flow1
            .combine(flow2) { f1, f2 -> "${f1}_${f2}" }
            .onEach { println(it) }
            .toList()

        /**
         * combine 에서는 A 는 쌍이 없는 상태에서 B가 왔기 때문에 A는 버린다
         *
         * .... A .... B .... C
         * .............. 1 ............. 2 .......... 3 .......... 4
         */
        combined shouldBeEqualTo listOf("B_1", "C_1", "C_2", "C_3", "C_4")
    }

    @Test
    fun `fold - accumulate all values in flow`() = runTest {
        val list = flowOf(1, 2, 3, 4)
            .onEach {
                delay(10)
                println("delay 1000, element=$it")
            }
        val res = list.fold(0) { acc, i -> acc + i }
        res shouldBeEqualTo 10
    }

    @Test
    fun `scan - sliding fold`() = runTest {
        val list = flowOf(1, 2, 3, 4)
        val res = list
            .onEach {
                delay(10)
                println("delay 1000, element=$it")
            }
            .scan(0) { acc, i -> acc + i }
            .toFastList()
        res shouldBeEqualTo listOf(0, 1, 3, 6, 10)
    }

    @Test
    fun `flatMapConcat - concat two flows`() = runTest {
        fun flowFrom(elem: String) = flowOf(1, 2, 3)
            .onEach {
                delay(100)
                println("delay 1000, $it")
            }
            .map { "${it}_${elem}" }

        val result = flowOf("A", "B", "C")
            .flatMapConcat { flowFrom(it) }
            .onEach { println(it) }
            .toFastList()

        result shouldBeEqualTo listOf("1_A", "2_A", "3_A", "1_B", "2_B", "3_B", "1_C", "2_C", "3_C")
    }

    @Test
    fun `flatMapMerge - merge two flows`() = runTest {
        fun flowFrom(elem: String) = flowOf(1, 2, 3)
            .onEach {
                delay(10)
                println("delay 10, $it")
            }
            .map { "${it}_${elem}" }

        val result = flowOf("A", "B", "C")
            .flatMapMerge(concurrency = 3) { flowFrom(it) }
            .onEach { print("$it, ") }
            .toFastList()

        result shouldBeEqualTo listOf("1_A", "1_B", "1_C", "2_A", "2_B", "2_C", "3_A", "3_B", "3_C")
    }

    @Test
    fun `flatMapMerge with concurrency - merge two flows`() = runTest {
        fun flowFrom(elem: String) = flowOf(1, 2, 3)
            .onEach {
                delay(10)
                println("delay 10, $it")
            }
            .map { "${it}_${elem}" }

        val result = flowOf("A", "B", "C")
            .flatMapMerge(concurrency = 2) { flowFrom(it) }
            .onEach { print("$it, ") }
            .toFastList()

        result shouldBeEqualTo listOf("1_A", "1_B", "2_A", "2_B", "3_A", "3_B", "1_C", "2_C", "3_C")
    }

    @Test
    fun `flatMapLatest - latest element with two flows`() = runTest {
        fun flowFrom(elem: String) = flowOf(1, 2, 3)
            .onEach {
                delay(10)
                println("delay 10, $it")
            }
            .map { "${it}_${elem}" }

        /**
         * ..........1_A..........2_A..........3_A
         * ..........1_B..........2_B..........3_B
         * ..........1_C..........2_C..........3_C
         */
        val result = flowOf("A", "B", "C")
            .flatMapLatest { flowFrom(it) }         // 각 flow의 마지막 요소들만 선택
            .onEach { println(it) }
            .toFastList()

        result shouldBeEqualTo listOf("1_C", "2_C", "3_C")
    }

    @Test
    fun `flatMapLatest - latest element with two flows with different delay`() = runTest {
        fun flowFrom(elem: String) = flowOf(1, 2, 3)
            .onEach {
                delay(1000)
                println("delay 1000")
            }
            .map { "${it}_${elem}" }

        /**
         * ..........1_A..........2_A..........3_A
         * ...............1_B..........2_B..........2_B
         * ................................1_C..........2_C..........3_C
         */
        val result = flowOf("A", "B", "C")
            .onEach { delay(1200); println("delay 1200") }
            .flatMapLatest { flowFrom(it) }         // 각 flow의 마지막 요소들만 선택
            .onEach { println(it) }
            .toFastList()

        result shouldBeEqualTo listOf("1_A", "1_B", "1_C", "2_C", "3_C")
    }

    @Test
    fun `reduce flow`() = runTest {
        val flow = flowOf(1, 2, 3, 4)

        flow.reduce { acc, i -> acc + i } shouldBeEqualTo 10
    }
}
