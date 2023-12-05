package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
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
 * Docker Compose Module을 이용하여 여러개의 서비스를 실행하는 테스트
 *
 * 참고 : [Docker Compose Module](https://www.testcontainers.org/modules/docker_compose/)
 */
@Testcontainers
class MultipleServiceTest {

    companion object: KLogging() {
        private const val REDIS_NAME = "redis_1"
        private const val REDIS_PORT = 6739

        private const val MONGO_NAME = "mongo_1"
        private const val MONGO_PORT = 27017

        private const val ELASTIC_NAME = "elasticsearch_1"
        private const val ELASTIC_PORT = 9200

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-multiple.yml"))
                // FIXME: Redis 7 이 testcontainers의 docker-compose 로는 실행이 안된다. (docker-compose 로는 실행이 된다.)
                // 이게 colima 문제인지 모르겠다.
                // .withExposedService(REDIS_NAME, REDIS_PORT, Wait.forHealthcheck())
                .withExposedService(MONGO_NAME, MONGO_PORT, Wait.forListeningPort())
                .withExposedService(
                    ELASTIC_NAME,
                    ELASTIC_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
        // .withLocalCompose(true) // docker-compose 가 설치되어 있어야 한다.
    }

    @Disabled("colima 때문인지? Redis 7 이 testcontainers의 docker-compose 로는 실행이 안된다. (docker-compose 로는 실행이 된다.)")
    @Test
    fun `load redis instance`() {
        val redisContainer: ContainerState = dockerComposeContainer.getContainerByServiceName(REDIS_NAME).orElseThrow()

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
        val elasticContainer = dockerComposeContainer.getContainerByServiceName(ELASTIC_NAME).orElseThrow()
        val host = elasticContainer.host
        val port = elasticContainer.getMappedPort(ELASTIC_PORT)

        log.debug { "elastic host: $host, port: $port" }
        elasticContainer.isRunning.shouldBeTrue()
    }
}
