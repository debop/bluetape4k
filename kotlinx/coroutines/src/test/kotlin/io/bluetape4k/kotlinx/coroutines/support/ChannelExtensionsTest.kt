package io.bluetape4k.kotlinx.coroutines.support

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import java.time.Duration
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ChannelExtensionsTest {

    companion object: KLogging()

    @Test
    fun `distinct until changed`() = runTest {
        val channel = produce {
            send(1)
            send(1)
            send(2)
            send(2)
            send(3)
            send(1)
        }
        yield()

        val distinct = channel.distinctUntilChanged()
        distinct.toList() shouldBeEqualTo listOf(1, 2, 3, 1)
    }

    @Test
    fun `distinct until changed by equal operator`() = runTest {
        val channel = produce {
            send(1.1)
            send(1.2)
            send(2.1)
            send(2.6)
            send(3.1)
            send(1.2)
        }
        yield()

        val distinct = channel.distinctUntilChanged { a, b ->
            a.toInt() == b.toInt()
        }

        distinct.toList() shouldBeEqualTo listOf(1.1, 2.1, 3.1, 1.2)
    }

    @Test
    fun `recude received element`() = runTest {
        val channel = produce {
            send(1)
            send(2)
            send(3)
        }

        val reduced = channel.reduce { acc, item -> acc + item }
        reduced.receive() shouldBeEqualTo 6
    }

    @Test
    fun `recude received element with initial value`() = runTest {
        val channel = produce {
            send(1)
            send(2)
            send(3)
        }

        val reduced = channel.reduce(0) { acc, item -> acc + item }
        reduced.receive() shouldBeEqualTo 6
    }

    @Test
    fun `concat channels`() = runTest {
        val channel1 = produce {
            send(1)
            send(2)
            send(3)
        }
        val channel2 = produce {
            send(4)
            send(5)
            send(6)
        }

        concat(channel1, channel2).toList() shouldBeEqualTo listOf(1, 2, 3, 4, 5, 6)
    }

    @Test
    fun `debounce elements`() = runSuspendTest {
        val channel = produce {
            send(1)
            delay(500)
            send(2)
            delay(100)
            send(3)
            delay(1500)
            send(4)
            delay(100)
            send(5)
        }

        val debounded = channel.debounce(Duration.ofMillis(1000))
        debounded.toList() shouldBeEqualTo listOf(1, 3, 4, 5)
    }
}
