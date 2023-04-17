package io.bluetape4k.examples.coroutines.guide

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ChannelExamples {
    companion object: KLogging()

    @Test
    fun `channel-01`() = runTest {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                delay(Random.nextLong(100))
                log.trace { "Send value=${x * x}" }
                channel.send(x * x)
            }
        }
        yield()

        repeat(5) {
            val received = channel.receive()
            log.trace { "received=$received" }
        }

        log.trace { "Done!" }
    }

    @Test
    fun `channel-02`() = runTest {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                delay(Random.nextLong(100))
                log.trace { "Send value=${x * x}" }
                channel.send(x * x)
            }
            // 접속 종료를 알린다 (reactive의 onCompletion)
            channel.close()
        }

        for (received in channel) {
            log.trace { "received=$received" }
        }

        log.trace { "Done!" }
    }

    @Test
    fun `channel-03`() {
        fun CoroutineScope.produceSquare(): ReceiveChannel<Int> = produce {
            for (x in 1..5) send(x * x)
        }

        runTest {
            val squares = produceSquare()
            squares.consumeEach {
                log.trace { "Received=$it" }
            }
            log.trace("Done!")
        }
    }
}
