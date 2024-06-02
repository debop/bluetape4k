package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.ShutdownQueue
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.redisson.Redisson
import org.redisson.config.Config
import org.testcontainers.containers.ContainerState
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration

@Testcontainers
class DockerComposeRedisTest {

    companion object: KLogging() {
        private const val REDIS_NAME = "redis"
        const val REDIS_PORT = 6379

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-redis.yml"))
                .withExposedService(
                    REDIS_NAME,
                    REDIS_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .apply {
                    ShutdownQueue.register(this)
                }
    }

    @Test
    fun `load redis instance`() {
        val redisContainer: ContainerState by lazy {
            dockerComposeContainer.getContainerByServiceName(REDIS_NAME).orElseThrow()
        }
        redisContainer.isRunning.shouldBeTrue()

        val host = redisContainer.host
        val port = redisContainer.getMappedPort(REDIS_PORT)
        log.debug { "redis host: $host, port: $port" }

        val redisson = Redisson.create(
            Config().apply {
                useSingleServer().apply {
                    address = "redis://$host:$port"
                }
            }
        )
        val map = redisson.getMap<String, String>("test")
        map.put("key", "value")
        map.get("key") shouldBeEqualTo "value"
    }
}
