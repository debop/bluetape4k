package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.ShutdownQueue
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Disabled
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

/**
 * Docker Compose Module을 이용하여 Docker Compose 파일에 정의된 서비스를 실행합니다.
 *
 * 참고 : [Docker Compose Module](https://www.testcontainers.org/modules/docker_compose/)
 */
@Testcontainers
class MultipleServiceTest {

    companion object: KLogging() {
        private const val REDIS_NAME = "redis_1"
        const val REDIS_PORT = 6739

        private const val ELASTICSEARCH_NAME = "elasticsearch_1"
        const val ELASTICSEARCH_PORT = 9200

        private const val POSTGRES_NAME = "postgres"
        private const val POSTGRES_PORT = 5432

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-multiple.yml"))
//                .withExposedService(
//                    REDIS_NAME,
//                    REDIS_PORT,
//                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
//                )
                .withExposedService(
                    ELASTICSEARCH_NAME,
                    ELASTICSEARCH_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .withExposedService(
                    POSTGRES_NAME,
                    POSTGRES_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .apply {
                    ShutdownQueue.register(this)
                }
    }

    @Disabled("Redis is not running")
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

    @Test
    fun `load elastic instance`() {
        val elasticContainer: ContainerState by lazy {
            dockerComposeContainer.getContainerByServiceName(ELASTICSEARCH_NAME).orElseThrow()
        }
        val host = elasticContainer.host
        val port = elasticContainer.getMappedPort(ELASTICSEARCH_PORT)

        log.debug { "elastic host: $host, port: $port" }
        elasticContainer.isRunning.shouldBeTrue()
    }

    @Test
    fun `load postgres container`() {
        val postgresContainer: ContainerState by lazy {
            dockerComposeContainer.getContainerByServiceName(POSTGRES_NAME).orElseThrow()
        }
        postgresContainer.isRunning.shouldBeTrue()

        val host = postgresContainer.host
        val port = postgresContainer.getMappedPort(POSTGRES_PORT)
        log.debug { "postgres host=$host, port=$$port" }
    }
}
