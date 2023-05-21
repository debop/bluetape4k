package io.bluetape4k.workshop.redis.examples.reactive.commands

import io.bluetape4k.io.getString
import io.bluetape4k.io.toByteBuffer
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8ByteBuffer
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.workshop.redis.examples.reactive.AbstractReactiveRedisTest
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.ReactiveRedisConnection
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand
import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.ByteBuffer
import java.time.Duration

class ReactiveKeyCommandsTest(
    @Autowired private val connectionFactory: ReactiveRedisConnectionFactory,
): AbstractReactiveRedisTest() {

    companion object: KLogging() {
        private val PREFIX = ReactiveKeyCommandsTest::class.simpleName!!
        private val KEY_PATTERN = "$PREFIX*"
        private const val KEY_SIZE = 50

        private const val REPEAT_SIZE = 3
    }

    private val connection: ReactiveRedisConnection by lazy {
        connectionFactory.reactiveConnection
    }
    private val serializer: RedisSerializer<String> = RedisSerializer.string()

    private suspend fun generateRandomKeys(size: Int) {
        val keys = flow {
            for (i in 1..size) {
                emit(serializer.serialize("$PREFIX-$i")!!.toByteBuffer())
            }
        }.map {
            SetCommand.set(it).value(Fakers.fixedString(128).toUtf8Bytes().toByteBuffer())
        }

        log.debug { "Generating $size keys" }
        connection.stringCommands().set(keys.asFlux()).awaitLast()
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            connection.serverCommands().flushAll().awaitSingle()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `keys - matching pattern`() = runSuspendWithIO {
        generateRandomKeys(KEY_SIZE)

        val keyCoount = connection.keyCommands()
            .keys(serializer.serialize(KEY_PATTERN)!!.toByteBuffer())
            .asFlow()
            .buffer()
            .flatMapConcat { it.asFlow() }
            .onEach { log.debug { it.getString() } }
            .count()

        keyCoount shouldBeEqualTo KEY_SIZE
    }

    @Test
    fun `store to list and pop`() = runSuspendWithIO {
        val key = serializer.serialize("list")!!.toByteBuffer()
        val value = listOf(serializer.serialize("item")!!.toByteBuffer())

        val popResult = connection.listCommands()
            .brPop(listOf(key), Duration.ofSeconds(1))

        val llen = connection.listCommands().lLen(key)

        // Reactive 나 CompletableFuture 나 뭐가 다른가?
        // flatMap == thenApply, thenCompose
        val popAndLength = connection.listCommands().rPush(key, value)
            .flatMap { popResult }
            .doOnNext {
                log.debug { "pop value: ${it.value.toUtf8String()}" }
            }
            .flatMap { llen }
            .doOnNext {
                log.debug { "Total items in list left: $it" }
            }
            .awaitSingle()

        popAndLength shouldBeEqualTo 0L
    }

    @Test
    fun `store to list and pop coroutine style`() = runSuspendWithIO {
        val key: ByteBuffer = "list".toUtf8ByteBuffer()
        val value: List<ByteBuffer> = listOf("item".toUtf8ByteBuffer())

        with(connection.listCommands()) {
            rPush(key, value).awaitSingle()

            val popResult = brPop(listOf(key), Duration.ofSeconds(1)).awaitSingle()
            log.debug { "pop value: ${popResult.value.toUtf8String()}" }

            val length = lLen(key).awaitSingle()
            log.debug { "Total items in list left: $length" }

            length shouldBeEqualTo 0L
        }
    }
}
