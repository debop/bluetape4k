package io.bluetape4k.examples.coroutines.flow

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

/**
 * 복수의 collector 에게 동시에 데이터를 전달하는 SharedFlow 를 사용하는 예제
 */
class SharedFlowExamples {

    companion object: KLogging()

    @Test
    fun `shared flow 기본 사용법`() = runTest {
        coroutineScope {
            val mutableSharedFlow = MutableSharedFlow<String>(replay = 0) // replay = 0: 캐시를 사용하지 않음

            launch {
                mutableSharedFlow.collect {
                    log.info { "#1 received $it" }
                }
            }

            launch {
                mutableSharedFlow.collect {
                    log.info { "#2 received $it" }
                }
            }

            delay(100)

            launch {
                mutableSharedFlow.test {
                    mutableSharedFlow.emit("Message1")
                    mutableSharedFlow.emit("Message2")

                    awaitItem() shouldBeEqualTo "Message1"
                    awaitItem() shouldBeEqualTo "Message2"
                    cancelAndConsumeRemainingEvents()
                }
            }
            delay(100)
            coroutineContext.cancelChildren()
        }
    }

    /**
     * SharedFlow 내부에 캐시를 사용해서 emit 이후에 collector 가 등록되더라도 이전에 emit 된 데이터 중 replay 숫자만큼 전달할 수 있다.
     */
    @Test
    fun `replay cache item in shared flow`() = runTest {
        val sharedFlow = MutableSharedFlow<String>(replay = 2)
        sharedFlow.emit("Message1")
        sharedFlow.emit("Message2")
        sharedFlow.emit("Message3")

        coroutineScope {
            sharedFlow.replayCache shouldBeEqualTo listOf("Message2", "Message3")

            launch {
                sharedFlow.collect {
                    log.info { "#1 received $it" }  // Message 2, Message 3
                }
            }
            launch {
                sharedFlow.collect {
                    log.info { "#2 received $it" }  // Message 2, Message 3
                }
            }
            delay(100)

            launch {
                sharedFlow.test {
                    sharedFlow.resetReplayCache() // 캐시를 초기화
                    sharedFlow.replayCache.shouldBeEmpty()
                    cancelAndConsumeRemainingEvents()
                }
            }

            delay(100)
            coroutineContext.cancelChildren()
        }
    }

    /**
     * 일반 flow 를 shared flow 로 변경하여, 복수의 collector 를 붙일 수 있다 (shareIn)
     *
     * - started: SharingStarted.Eagerly: flow 가 시작되면, collector 가 붙을 때까지 기다리지 않고, 바로 시작
     * - replay: 0: 캐시를 사용하지 않음
     *
     * 결과
     *
     * ```
     * (1 sec)
     * #1 A             // #1 collector 만 있음
     * (1 sec)
     * #1 B
     * #2 B             // #2 collector 가 붙음
     * (1 sec)
     * #1 C
     * #2 C
     * #3 C             // #3 collector 가 붙음
     * ```
     */
    @Test
    fun `shareIn - change flow to shared flow`() = runTest {
        val flow = flowOf("A", "B", "C")
            .onEach {
                delay(1000)
                log.debug { "(1 sec)" }
            }

        val sharedFlow = flow.shareIn(this, started = SharingStarted.Eagerly, replay = 0)

        delay(500)
        launch {
            sharedFlow.collect { log.info { "#1 $it" } }
        }

        delay(1000)
        launch {
            sharedFlow.collect { log.info { "#2 $it" } }
        }

        delay(1000)
        launch {
            sharedFlow.collect { log.info { "#3 $it" } }
        }

        delay(5000)
        coroutineContext.cancelChildren()
    }
}
