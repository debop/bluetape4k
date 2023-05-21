package io.bluetape4k.workshop.redis.examples.reactive.operations

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.redis.examples.reactive.AbstractReactiveRedisTest
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import java.time.Duration
import kotlin.system.measureTimeMillis

class ValueOperationsTest(
    @Autowired private val operations: ReactiveRedisOperations<String, String>,
): AbstractReactiveRedisTest() {

    companion object: KLogging() {
        private const val CACHED_VALUE = "Hello, World!"
    }

    private val valueCreationCounter = atomic(0)

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            // TODO: ReactiveRedisOperations 에 대해 `executeSuspending` 함수를 만들자 
            operations.execute { connection ->
                connection.serverCommands().flushAll()
            }.awaitSingle() shouldBeEqualTo "OK"
        }
        valueCreationCounter.value = 0
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
    }

    @Test
    fun `simple caching sequence using GET and SETX commnads`() = runSuspendWithIO {
        val cacheKey = Fakers.fixedString(64)

        val elapsed = measureTimeMillis {
            val valueOperations = operations.opsForValue()
            var value = valueOperations.get(cacheKey).awaitSingleOrNull()
            if (value == null) {
                value = cacheValue()
                valueOperations.set(cacheKey, value, Duration.ofSeconds(10)).awaitSingle()
            }

            value shouldBeEqualTo CACHED_VALUE

            val value2 = valueOperations.get(cacheKey).awaitSingleOrNull()
            value2 shouldBeEqualTo CACHED_VALUE
        }
        // cacheValue() 실행이 3초가 걸리므로
        elapsed shouldBeLessOrEqualTo 5_000L
        valueCreationCounter.value shouldBeEqualTo 1
    }

    private suspend fun cacheValue(): String {
        delay(Duration.ofSeconds(3))
        valueCreationCounter.incrementAndGet()
        return CACHED_VALUE
    }
}
