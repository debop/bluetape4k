package io.bluetape4k.examples.coroutines.flow

import app.cash.turbine.test
import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.coroutines.flow.eclipse.asFlow
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class FlowBuilderExamples {

    companion object: KLogging()

    @Test
    fun `flowOf - with specific elements`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)   // like Flux.just(...)
        var collected = 0
        flow.collect {
            collected = it
            log.debug { "element=$it" }
        }
        collected shouldBeEqualTo flow.last()
    }

    @Test
    fun `empty flow`() = runTest {
        emptyFlow<Int>()
            .collect {
                fail("아무것도 실행되면 안됩니다.")
            }
    }

    @Test
    fun `convert list to flow - asFlow`() = runTest {
        var count = 0
        intArrayListOf(1, 2, 3, 4, 5)
            .asFlow()
            .collect {
                count++
                log.debug { "element=$it" }
            }

        count shouldBeEqualTo 5
    }

    /**
     * suspend 함수를 flow 로 만들 수 있다
     */
    @Test
    fun `convert function to flow`() = runTest {
        val function: suspend () -> String = suspend {
            delay(1000)
            "UserName"
        }

        function.asFlow()
            .collect {
                log.debug { "element=$it" }
                it shouldBeEqualTo "UserName"
            }
    }

    private suspend fun getUserName(): String {
        delay(1000)
        return "UserName"
    }

    @Test
    fun `convert regular function to flow`() = runTest {
        ::getUserName.asFlow()
            .collect {
                log.debug { "element=$it" }
                it shouldBeEqualTo "UserName"
            }
    }

    @Test
    fun `flow builder`() = runTest {
        val nums = flow {
            repeat(3) { num ->
                delay(10)
                emit(num)
            }
        }
        val counter1 = atomic(0)
        val counter2 = atomic(0)
        // flow 를 여러 subscriber 가 중복해서 받아갈 수 있다
        coroutineScope {
            launch {
                nums.collect {
                    log.debug { "Job1 element=$it" }
                    counter1.incrementAndGet()
                }
            }
            launch {
                // delay 를 줘도 flow 는 cold stream 이므로, buffer 에 쌓여있는 값들은 모두 처리됩니다.
                delay(15)
                nums.collect {
                    log.debug { "Job2 element=$it" }
                    counter2.incrementAndGet()
                }
            }
            // https://github.com/cashapp/turbine/
            // turbine 을 이용하여 assertions 를 수행할 수 있습니다.
            // flow 는 cold stream 이므로 반복적으로 collect 할 수 있습니다.
            launch {

                nums.test {
                    awaitItem() shouldBeEqualTo 0
                    awaitItem() shouldBeEqualTo 1
                    awaitItem() shouldBeEqualTo 2
                    awaitComplete()
                }
            }
            launch {
                nums.test {
                    awaitItem() shouldBeEqualTo 0
                    awaitItem() shouldBeEqualTo 1
                    awaitItem() shouldBeEqualTo 2
                    awaitComplete()
                }
            }
        }
        counter1.value shouldBeEqualTo 3
        counter2.value shouldBeEqualTo 3
    }
}
