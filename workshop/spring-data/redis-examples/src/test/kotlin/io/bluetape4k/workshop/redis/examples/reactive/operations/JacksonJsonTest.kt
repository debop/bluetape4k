package io.bluetape4k.workshop.redis.examples.reactive.operations

import io.bluetape4k.io.getBytes
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8ByteBuffer
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.workshop.redis.examples.reactive.AbstractReactiveRedisTest
import io.bluetape4k.workshop.redis.examples.reactive.model.EmailAddress
import io.bluetape4k.workshop.redis.examples.reactive.model.Person
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations

class JacksonJsonTest @Autowired constructor(
    private val typedOperations: ReactiveRedisOperations<String, Person>,
    private val genericOperations: ReactiveRedisOperations<String, Any?>,
): AbstractReactiveRedisTest() {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            // TODO: ReactiveRedisOperations 에 대해 `executeSuspending` 함수를 만들자
            genericOperations.execute { connection ->
                connection.serverCommands().flushAll()
            }.awaitSingle() shouldBeEqualTo "OK"
        }
    }

    @Test
    fun `context loading`() {
        typedOperations.shouldNotBeNull()
        genericOperations.shouldNotBeNull()
    }

    /**
     * [ReactiveRedisOperations] using [String] keys and [Person] values serialized via
     * [org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer] to JSON
     * without additional type hints.
     *
     * @see [io.bluetape4k.workshop.redis.examples.reactive.ReactiveRedisConfiguration.reactiveJsonPersonRedisTemplate]
     */
    @Test
    fun `write and read person`() = runSuspendTest {
        val homer = Person("Homer", "Simpson")

        typedOperations.opsForValue().set("homer", homer).awaitSingle().shouldBeTrue()

        // Value 를 String 으로 가져오기
        val value = typedOperations
            .execute { conn ->
                conn.stringCommands().get("homer".toUtf8ByteBuffer())
            }
            .map { buffer -> buffer.getBytes().toUtf8String() }
            .awaitSingle()

        value shouldBeEqualTo """{"firstname":"Homer","lastname":"Simpson"}"""

        // Value 를 Person 객체로 가져오기  
        typedOperations.opsForValue().get("homer").awaitSingle() shouldBeEqualTo homer
    }

    /**
     * [ReactiveRedisOperations] using [String] keys and [Any] values serialized via
     * [org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer] to JSON with additional type
     * hints. This example uses the non-final type [Person] using its FQCN as type identifier.
     *
     * @see [io.bluetape4k.workshop.redis.examples.reactive.RedisTestConfiguration.reactiveJsonObjectRedisTemplate]
     */
    @Test
    fun `write and read person as object`() = runSuspendTest {
        val homer = Person("Homer", "Simpson")

        genericOperations.opsForValue().set("homer", homer).awaitSingle().shouldBeTrue()

        // Value 를 String 으로 가져오기
        val value = typedOperations
            .execute { conn ->
                conn.stringCommands().get("homer".toUtf8ByteBuffer())
            }
            .map { buffer -> buffer.getBytes().toUtf8String() }
            .awaitSingle()

        value shouldBeEqualTo """{"@class":"io.bluetape4k.workshop.redis.examples.reactive.model.Person","firstname":"Homer","lastname":"Simpson"}"""

        // Value 를 @class 에 있는 class 정보를 바탕으로 객체로 가져오기
        val value2 = genericOperations.opsForValue().get("homer").awaitSingle()
        value2.shouldNotBeNull() shouldBeInstanceOf Person::class
        value2 shouldBeEqualTo homer
    }

    /**
     * [ReactiveRedisOperations] using [String] keys and [Any] values serialized via
     * [org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer] to JSON with additional type
     * hints. This example uses the final type [EmailAddress] using configuration from
     * [com.fasterxml.jackson.annotation.JsonTypeInfo] as type identifier.
     *
     * @see [io.bluetape4k.workshop.redis.examples.reactive.RedisTestConfiguration.reactiveJsonObjectRedisTemplate]
     */
    @Test
    fun `write and read email object`() = runSuspendTest {
        val email = EmailAddress("homer@the-simpson.com")

        genericOperations.opsForValue().set("mail", email).awaitSingle().shouldBeTrue()

        // Value 를 String 으로 가져오기
        val value = typedOperations
            .execute { conn ->
                conn.stringCommands().get("mail".toUtf8ByteBuffer())
            }
            .map { buffer -> buffer.getBytes().toUtf8String() }
            .awaitSingle()

        value shouldBeEqualTo """{"@class":"io.bluetape4k.workshop.redis.examples.reactive.model.EmailAddress","address":"homer@the-simpson.com"}"""

        genericOperations.opsForValue().get("mail").awaitSingle() shouldBeEqualTo email
    }
}
