package io.bluetape4k.workshop.bucket4j

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class RedisApplication {

    companion object: KLogging() {
        private val redisServer = RedisServer.Launcher.redis
    }
}

fun main(vararg args: String) {
    runApplication<RedisApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
