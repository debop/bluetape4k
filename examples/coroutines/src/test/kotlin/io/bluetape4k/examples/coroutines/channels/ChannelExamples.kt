package io.bluetape4k.examples.coroutines.channels

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ChannelExamples {

    companion object: KLogging()


    @Test
    fun `basic channel example`() = runTest {
        coroutineScope {
            val channel = Channel<Int>()

            launch {
                repeat(5) { index ->
                    delay(1000)
                    log.debug { "Producing next one. $index" }
                    channel.send(index * 2)
                }
            }

            launch {
                repeat(5) {
                    val received = channel.receive()
                    log.debug { "Receive $received" }
                }
            }
        }
    }

    @Test
    fun `foreach 구문으로 수신하기`() = runTest {
        coroutineScope {
            val channel = Channel<Int>()

            launch {
                repeat(5) { index ->
                    delay(1000)
                    log.debug { "Producing next one. $index" }
                    channel.send(index * 2)
                }
                // channel#close() 를 호출해야 for each 구문을 끝낼 수 있습니다.
                channel.close()
            }

            launch {
                for (element in channel) {
                    log.debug { "Receive $element" }
                }
            }
        }
    }

    @Test
    fun `consumeEach 구문으로 수신하기`() = runTest {
        coroutineScope {
            val channel = Channel<Int>()

            launch {
                repeat(5) { index ->
                    delay(1000)
                    log.debug { "Producing next one. $index" }
                    channel.send(index * 2)
                }
                // channel#close() 를 호출해야 consumeEach 구문을 끝낼 수 있습니다.
                // 예외 시에 문제가 될 수도 있죠 --> produce 함수를 사용하는 걸 추천합니다.
                channel.close()
            }

            launch {
                channel.consumeEach { element ->
                    log.debug { "Receive $element" }
                }
            }
        }
    }

    @Test
    fun `produce 함수를 이용하여 channel 구성`() = runTest {
        val channel = produce {
            repeat(5) { index ->
                delay(1000)
                log.debug { "Producing next one. $index" }
                send(index)
            }
        }

        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    /**
     * Channel Type
     *
     * Unlimited: 버퍼가 무한대
     *
     * Buffered: 기본 64개의 버퍼를 둔다. 버퍼를 초과하면 send 가 suspend 된다
     *
     * Rendezvous (기본): 버퍼가 0로서 send 가 있어야 receive 가 되고, receive 를 안하면 send는 suspend 된다
     *
     * Conflated: 버퍼가 1이고, 새로운 send가 있다면 기존 버퍼의 요소를 대체한다
     *
     */

    @Test
    fun `buffer type - unlimited`() = runTest {
        val channel = produce(capacity = Channel.UNLIMITED) {
            repeat(5) { index ->
                send(index * 2)
                delay(100)
                log.debug { "Sent ${index * 2}" }
            }
        }
        // send한 요소가 모두 버퍼링 된다
        delay(1000)
        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    @Test
    fun `buffer type - buffered`() = runTest {
        val channel = produce(capacity = 3) {
            repeat(5) { index ->
                send(index * 2)
                delay(100)
                log.debug { "Sent ${index * 2}" }
            }
        }
        // send한 요소가 모두 버퍼링 된다
        delay(1000)
        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    @Test
    fun `buffer type - rendezvous`() = runTest {
        val channel = produce(capacity = Channel.RENDEZVOUS) {
            repeat(5) { index ->
                send(index * 2)
                delay(100)
                log.debug { "Sent ${index * 2}" }
            }
        }
        // send한 요소가 모두 버퍼링 된다
        delay(1000)
        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    @Test
    fun `buffer type - conflated`() = runTest {
        val channel = produce(capacity = Channel.CONFLATED) {
            repeat(5) { index ->
                send(index * 2)
                delay(100)
                log.debug { "Sent ${index * 2}" }
            }
        }
        // send한 요소가 모두 버퍼링 된다
        delay(1000)
        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    /**
     * onBufferOverflow
     * SUSPEND (default) : 버퍼가 찼다면 대기한다
     * DROP_OLDEST: 가장 오래된 것부터 삭제한다
     * DROP_LATEST: 가장 최근 것부터 삭제한다
     */
    @Test
    fun `onBufferOverflow options`() = runTest {
        // 0,2,4,6,8 -> 6,8 만 남는다 (예전 것을 삭제하므로)
        val channel = Channel<Int>(capacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST) {
            log.warn { "Undelivered element: $it" }
        }
        launch {
            repeat(5) { index ->
                channel.send(index * 2)
                delay(100)
                log.debug { "Sent ${index * 2}" }
            }
            channel.close()
        }
        // send한 요소가 모두 버퍼링 된다
        delay(1000)
        for (element in channel) {
            log.debug { "Receive $element" }
        }
    }

    /**
     * Fan-out 은 여러 개의 Consumer가 Channel 요소를 분배해서 수신합니다.
     */
    @Nested
    inner class Fanout {

        private fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
            repeat(10) {
                delay(100)
                send(it)
            }
        }

        private fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) {
            launch {
                for (msg in channel) {
                    log.debug { "#$id received $msg" }
                }
            }
        }

        @Test
        fun `fan-out with single channel`() = runTest {
            val channel = produceNumbers()

            repeat(3) { id ->
                delay(10)
                launchProcessor(id, channel)
            }
        }
    }

    /**
     * Fan-in 은 복수의 producer 가 하나의 channel 에 요소를 전송합니다
     *
     * @constructor Create empty Fan in
     */
    @Nested
    inner class FanIn {

        private suspend fun sendString(
            channel: SendChannel<String>,
            text: String,
            time: Long = 100,
        ) {
            while (true) {
                delay(time)
                channel.send(text)
            }
        }

        @Test
        fun `fan-in with multiple send channel`() = runTest {
            val channel = Channel<String>()
            launch { sendString(channel, "foo", 200L) }
            launch { sendString(channel, "BAR!", 500L) }

            repeat(50) {
                log.debug { channel.receive() }
            }
            // channel의 전송 작업을 취소시킵니다.
            coroutineContext.cancelChildren()
        }

        /**
         * 여러 채널로부터 정보를 받아 하나의 채널에 전송합니다.
         */
        private fun <T> CoroutineScope.fanIn(
            channels: List<ReceiveChannel<T>>,
        ): ReceiveChannel<T> = produce {
            channels.forEach { channel ->
                // launch 를 써서 병렬로 실행하도록 해야 합니다.
                launch {
                    for (elem in channel) {
                        send(elem)
                    }
                }
            }
        }

        @Test
        fun `여러 채널로부터 들어오는 정보를 하나의 채널로 fan-in 한다`() = runTest {
            val channels = List(3) { Channel<String>() }

            val fanin = fanIn(channels)

            channels.forEach { channel ->
                launch {
                    sendString(channel, UUID.randomUUID().encodeBase62(), 200L)
                }
            }

            repeat(50) {
                log.debug { fanin.receive() }
            }
            // channel의 전송 작업을 취소시킵니다.
            coroutineContext.cancelChildren()
        }
    }

    @Nested
    inner class Pipeline {

        private fun CoroutineScope.numbers(times: Int = 5): ReceiveChannel<Int> = produce<Int> {
            repeat(times) { num ->
                send(num + 1)
            }
        }

        private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce<Int> {
            for (num in numbers) {
                send(num * num)
            }
        }

        @Test
        fun `pipeline with two channel chaining`() = runTest {
            val numbers = numbers(10)
            val squared = square(numbers)

            for (num in squared) {
                log.debug { "Squared=$num" }
            }
        }
    }
}
