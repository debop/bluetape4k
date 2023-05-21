package io.bluetape4k.workshop.redis.examples.reactive.operations

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.workshop.redis.examples.reactive.AbstractReactiveRedisTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import java.time.Duration
import java.util.logging.Level

class ListOperationsTest(
    @Autowired private val operations: ReactiveRedisOperations<String, String>,
): AbstractReactiveRedisTest() {

    companion object: KLogging() {
        private const val MESSAGE = "Hello World"
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.execute { conn ->
                conn.serverCommands().flushDb()
            }.awaitSingle() shouldBeEqualTo "OK"
        }
    }

    /**
     * A simple queue using Redis blocking list commands `BLPOP` and `LPUSH` to produce the queue message.
     */
    @Test
    fun `poll and populate queue`() = runSuspendWithIO {
        val queue = "simple-queue"

        val listOps = operations.opsForList()

        val blpop = listOps.leftPop(queue, Duration.ofSeconds(5))
            .log("workshop.redis.examples.reactive", Level.INFO)

        log.info { "Blocing pop ... waiting for message" }

        delay(3)
        listOps.leftPush(queue, MESSAGE).awaitSingle() shouldBeEqualTo 1
        log.info { "Blocking pop ... done!" }

        blpop.awaitSingle() shouldBeEqualTo MESSAGE
    }
}
