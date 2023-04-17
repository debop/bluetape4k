package io.bluetape4k.examples.coroutines.flow

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class StateFlowExamples {

    companion object: KLogging()

    @Test
    fun `StateFlow 값 변화 관찰하기 - 상태 전파`() = runTest {
        val state = MutableStateFlow(1)

        launch {
            state.collect {
                log.info { "Value changed to $it" }
            }
        }
        delay(100)
        state.value = 2

        delay(100)
        launch {
            state.collect {
                log.info { "and now it is $it" }
            }
        }
        delay(100)
        state.value = 3

        delay(1000)
        coroutineContext.cancelChildren()
    }

    @Test
    fun `stateIn - 일반 flow 를 StateFlow로 변환하기`() = runTest {
        val flow = flowOf("A", "B", "C")
            .onEach { delay(1000); log.info { "delay 1000" } }
            .onEach { log.info { "Produced $it" } }

        val stateFlow = flow.stateIn(this)

        log.info { "Listening" }
        log.info { "State=${stateFlow.value}" }

        launch {
            stateFlow.collect {
                log.info { "Received $it" }
            }
        }
        delay(5000)
        log.info { "State=${stateFlow.value}" }
        coroutineContext.cancelChildren()
    }
}
