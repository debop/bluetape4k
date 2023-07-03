package io.bluetape4k.workshop.bucket4j

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.lettuce.core.RedisClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LettuceConfiguration {

    companion object: KLogging()

    @Bean
    fun redisClient(): RedisClient {
        val url = System.getProperty("testcontainers.redis.url")
        log.debug { "Create RedisClient. url=$url" }
        return RedisClient.create(url)
    }
}
