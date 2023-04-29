package io.bluetape4k.coroutines.tests

import app.cash.turbine.test
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class TurbineExamples {

    companion object: KLogging()

    @Test
    fun `turbine을 이용하여 flow를 테스트`() = runTest {
        flowOf("one", "two").test {
            awaitItem() shouldBeEqualTo "one"
            awaitItem() shouldBeEqualTo "two"
            awaitComplete()
        }
    }

    @Test
    fun `flow가 예외를 발생시키면 raise 된다`() = runTest {
        // Flow 안에서 예외 발생 시 검출하고, stacktrace 정보로 알 수 있다
        assertFailsWith<AssertionError> {
            flow<Int> {
                throw RuntimeException("Boom!")
            }.test {
                // Nothing to do
            }
        }.cause shouldBeInstanceOf RuntimeException::class
    }

    @Test
    fun `비동기 flow 의 emit 작업을 대기한다`() = runTest {
        channelFlow {
            withContext(Dispatchers.IO) {
                Thread.sleep(10)
                send("item")
            }
        }.test {
            awaitItem() shouldBeEqualTo "item"
            awaitComplete()
        }
    }

    @Test
    fun `비동기 flow 내부에서 emit을 지연시키기`() = runTest {
        channelFlow {
            delay(10)
            send("item")
        }.test {
            awaitItem() shouldBeEqualTo "item"
            awaitComplete()
        }
    }

    @Test
    fun `turbine의 최대 대기시간은 1초이다`() = runTest(dispatchTimeoutMs = 500) {
        channelFlow {
            withContext(Dispatchers.IO) {
                Thread.sleep(10)
                send("item")
            }
        }.test {
            awaitItem() shouldBeEqualTo "item"
            awaitComplete()
        }
    }

    @Test
    fun `비동기 flow 처리를 취소할 수 있다`() = runTest {
        channelFlow {
            // 10개를 모두 send 해버립니다.
            withContext(Dispatchers.IO) {
                repeat(10) {
                    Thread.sleep(100)
                    log.trace { "Sending item $it" }
                    send("item $it")
                }
            }
        }.test {
            awaitItem() shouldBeEqualTo "item 0"
            awaitItem() shouldBeEqualTo "item 1"
            awaitItem() shouldBeEqualTo "item 2"
            cancel()
        }
    }

    @Test
    fun `delay되는 비동기 flow 처리를 취소할 수 있다`() = runTest {
        channelFlow {
            // 3개만 send하고, 더 이상 send 하지 않습니다.
            repeat(10) {
                delay(100)
                log.trace { "Sending item $it" }
                send("item $it")
            }
        }.test {
            awaitItem() shouldBeEqualTo "item 0"
            awaitItem() shouldBeEqualTo "item 1"
            awaitItem() shouldBeEqualTo "item 2"
            cancel()
        }
    }
}
