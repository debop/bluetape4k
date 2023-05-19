package io.bluetape4k.workshop.docker.compose

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.config.Config

class DockerComposePluginTest {

    companion object: KLogging()

    /**
     * Gradle Docker Compose Plugin 에서 docker service 에 대한 환경 정보를 system property 로 설정하기 때문에 그 값을 가져온다
     *
     * 참고: [Gradle Docker Compose Plugin](https://github.com/avast/gradle-docker-compose-plugin)
     */
    private val redisUrl: String get() = System.getProperty("redis.url")!!

    @Test
    fun `connect to redis`() {
        log.debug { "Connect to redis: $redisUrl" }
        val config = Config().apply { useSingleServer().address = redisUrl }

        val redisson = Redisson.create(config)
        redisson.keys.flushdb()

        val settings = redisson.getMap<String, String>("settings")
        settings["key1"] = "value1"
        settings["key2"] = "value2"

        settings["key1"] shouldBeEqualTo "value1"
    }
}
