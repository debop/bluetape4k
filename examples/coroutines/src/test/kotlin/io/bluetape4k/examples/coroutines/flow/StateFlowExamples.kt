package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class StateFlowExamples {

    companion object: KLogging()

    @Test
    fun `StateFlow 값 변화 관찰하기 - 상태 전파`() = runTest {
        val state = MutableStateFlow(1)
        val changeCounter1 = atomic(0)
        val changeCounter2 = atomic(0)

        launch {
            state.collect {
                log.info { "Value changed to $it" }
                changeCounter1.incrementAndGet()
            }
        }
        delay(10)
        state.value = 2

        delay(10)
        launch {
            state.collect {
                log.info { "and now it is $it" }
                changeCounter2.incrementAndGet()
            }
        }
        delay(10)
        state.value = 3

        delay(10)
        changeCounter1.value shouldBeEqualTo 3
        changeCounter2.value shouldBeEqualTo 2

        // 자식 Job 들을 모두 취소한다 
        coroutineContext.cancelChildren()
    }

    @Test
    fun `stateIn - 일반 flow 를 StateFlow로 변환하기`() = runTest {
        val flow = flowOf("A", "B", "C")
            .onEach {
                delay(100)
                log.info { "delay 100" }
            }
            .onEach { log.info { "Produced $it" } }

        val stateFlow = flow.stateIn(this)

        log.info { "Listening" }
        log.info { "State=${stateFlow.value}" }

        val receivedCounter = atomic(0)

        launch {
            stateFlow.collect {
                log.info { "Received $it" }
                receivedCounter.incrementAndGet()
            }
        }
        delay(500)
        log.info { "State=${stateFlow.value}" }
        receivedCounter.value shouldBeEqualTo 3

        coroutineContext.cancelChildren()
    }
}
