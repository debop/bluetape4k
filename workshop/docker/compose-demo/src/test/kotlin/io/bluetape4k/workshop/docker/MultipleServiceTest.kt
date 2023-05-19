package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
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
class MultipleServiceTest {

    companion object: KLogging() {
        const val REDIS_PORT = 6739
        const val ELASTIC_PORT = 9200

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-multiple.yml"))
                .withExposedService("redis_1", REDIS_PORT, Wait.forListeningPort())
                .withExposedService(
                    "elasticsearch_1",
                    ELASTIC_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .withLocalCompose(true)
    }

    @Test
    fun `load redis instance`() {
        val redisContainer: ContainerState = dockerComposeContainer.getContainerByServiceName("redis_1").orElseThrow()

        val host = redisContainer.host
        val port = redisContainer.getMappedPort(REDIS_PORT)

        log.debug { "redis host: $host, port: $port" }
        redisContainer.isRunning.shouldBeTrue()

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

    @Test
    fun `load elastic instance`() {
        val elasticContainer = dockerComposeContainer.getContainerByServiceName("elasticsearch_1").orElseThrow()
        val host = elasticContainer.host
        val port = elasticContainer.getMappedPort(ELASTIC_PORT)

        log.debug { "elastic host: $host, port: $port" }
        elasticContainer.isRunning.shouldBeTrue()
    }
}
