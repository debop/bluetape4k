package io.bluetape4k.workshop.cloud.gateway.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.lettuce.core.RedisClient
import org.springframework.context.annotation.Bean

// @Configuration(proxyBeanMethods = false)
class Bucket4jLettuceConfiguration {

    companion object: KLogging()

    @Bean
    fun redisClient(): RedisClient {
        val url = System.getProperty("testcontainers.redis.url")
        log.debug { "Create RedisClient. url=$url" }
        return RedisClient.create(url)
    }
}
