package io.bluetape4k.workshop.redis.examples.reactive

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.testcontainers.storage.RedisServer
import io.bluetape4k.workshop.redis.examples.reactive.model.Person
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import jakarta.annotation.PreDestroy

/**
 * [RedisApplication] 과 분리해서 독립적으로 테스트하기 위해서 [SpringBootApplication] 을 선언합니다.
 */
@SpringBootApplication
class ReactiveRedisConfiguration {

    companion object: KLogging() {
        @JvmStatic
        val redis = RedisServer.Launcher.redis

        @JvmStatic
        val faker = Fakers.faker
    }

    @Autowired
    private val reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory = uninitialized()

    /**
     * Configures a [ReactiveRedisTemplate] with [String] keys and a typed [Jackson2JsonRedisSerializer].
     */
    @Bean
    fun reactiveJsonPersonRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Person> {
        val valueSerializer = Jackson2JsonRedisSerializer(Person::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, Person>(RedisSerializer.string())
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    /**
     * Configures a [ReactiveRedisTemplate] with [String] keys and [GenericJackson2JsonRedisSerializer].
     *
     * NOTE: Value에 해당하는 Object 는 `@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)` 를 선언해야 한다.
     * 참고: [io.bluetape4k.workshop.redis.examples.reactive.model.EmailAddress]
     */
    @Bean
    fun reactiveJsonObjectRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any?> {
        // Value에 해당하는 Object 는 `@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)` 가 선언되어 있어야만 Class 정보를 알 수 있다
        val valueSerializer = GenericJackson2JsonRedisSerializer("@class")

        val context = RedisSerializationContext
            .newSerializationContext<String, Any?>(RedisSerializer.string())
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }

    @PreDestroy
    fun flushTestDb() {
        runBlocking {
            reactiveRedisConnectionFactory.reactiveConnection.serverCommands().flushDb().awaitSingleOrNull()
        }
    }
}
