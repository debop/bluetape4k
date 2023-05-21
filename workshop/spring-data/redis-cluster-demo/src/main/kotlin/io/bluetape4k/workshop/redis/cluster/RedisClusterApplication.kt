package io.bluetape4k.workshop.redis.cluster

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisClusterServer
import io.lettuce.core.resource.ClientResources
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class RedisClusterApplication {

    companion object: KLogging() {
        val redisCluster = RedisClusterServer.Launcher.redisCluster
    }

    @Bean(destroyMethod = "shutdown")
    fun lettuceClientResource(): ClientResources {
        return RedisClusterServer.Launcher.LettuceLib.clientResources(redisCluster)
    }
}

fun main(vararg args: String) {
    runApplication<RedisClusterApplication>(*args)
}
