package io.bluetape4k.workshop.redis.examples.reactive.operations

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.redis.examples.reactive.AbstractReactiveRedisTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
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
     * A simple queue using Redis blocking list commands `BRPOP` and `LPUSH` to produce the queue message.
     */
    @Test
    fun `poll and populate queue`() = runSuspendWithIO {
        val queue = "simple-queue"
        val listOps = operations.opsForList()

        // BRPOP
        val brpop = listOps.rightPop(queue, Duration.ofSeconds(5))
            .log("workshop.redis.examples.reactive", Level.INFO)
        log.debug { "BRPOP ... wating for message" }
        delay(5)

        // LPUSH
        listOps.leftPush(queue, MESSAGE).awaitSingle() shouldBeEqualTo 1
        delay(5)

        val message = brpop.awaitSingleOrNull()
        log.debug { "BRPOP ... done! message=$message" }
        message shouldBeEqualTo MESSAGE
    }
}
