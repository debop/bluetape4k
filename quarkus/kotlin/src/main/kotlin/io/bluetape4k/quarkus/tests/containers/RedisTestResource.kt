package io.bluetape4k.quarkus.tests.containers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.testcontainers.storage.RedisServer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

/**
 * Quarkus 테스트 시 Redis 서버를 사용할 수 있도록 해주는 [QuarkusTestResourceLifecycleManager] 구현체입니다.
 */
class RedisTestResource: QuarkusTestResourceLifecycleManager {

    companion object: KLogging() {
        val redis by lazy { RedisServer.Launcher.redis }

        val url: String get() = redis.url
        val port: Int get() = redis.port
    }

    override fun start(): MutableMap<String, String> {
        log.info { "Start Redis test resource ..." }

        redis.start()
        return mutableMapOf(
            "quarkus.redis.url" to redis.url
        )
    }

    override fun stop() {
        log.info { "Stop Redis test resource ..." }
        runCatching { redis.stop() }
    }
}
