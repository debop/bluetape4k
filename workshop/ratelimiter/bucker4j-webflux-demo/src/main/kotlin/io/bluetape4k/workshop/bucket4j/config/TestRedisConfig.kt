package io.bluetape4k.workshop.bucket4j.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.sysProperty
import io.bluetape4k.testcontainers.storage.RedisServer
import io.bluetape4k.utils.ShutdownQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order

@Configuration
@Profile("dev", "test")
@Order(0)
class TestRedisConfig {

    companion object: KLogging() {
        val redis = RedisServer(useDefaultPort = true).apply {
            start()
            ShutdownQueue.register(this)
        }
    }

    @Bean
    fun redisServer(): RedisServer {
        if (redis.isRunning.not()) {
            redis.start()
        }
        log.info { "Redis Server=${redis.url}" }

        sysProperty["spring.data.redis.url"] = redis.url
        return redis
    }
}
