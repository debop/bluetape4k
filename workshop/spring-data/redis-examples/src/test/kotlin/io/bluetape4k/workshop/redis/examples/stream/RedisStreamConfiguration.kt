package io.bluetape4k.workshop.redis.examples.stream

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.testcontainers.storage.RedisServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.StreamReceiver

/**
 * [io.bluetape4k.workshop.redis.examples.reactive.ReactiveRedisConfiguration] 에
 */
@SpringBootApplication
class RedisStreamConfiguration {

    companion object: KLogging() {
        @JvmStatic
        val redis = RedisServer.Launcher.redis
    }

    @Autowired
    private val factory: RedisConnectionFactory = uninitialized()

    @Autowired
    private val reactiveFactory: ReactiveRedisConnectionFactory = uninitialized()

    @Bean
    fun streamMessageListenerContainer(): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        return StreamMessageListenerContainer.create(factory)
    }

    /**
     * Redis Stream 을 Reactive 방식으로 읽기 위한 [StreamReceiver]를 생성합니다.
     */
    @Bean
    fun streamReceiver(): StreamReceiver<String, MapRecord<String, String, String>> {
        return StreamReceiver.create(reactiveFactory)
    }
}
