package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ContainerState
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration

/**
 * Docker Compose Module을 이용하여 서비스를 실행하는 테스트
 *
 * 참고 : [Docker Compose Module](https://www.testcontainers.org/modules/docker_compose/)
 */
@Testcontainers
class SimpleIntegrationTest {

    companion object: KLogging() {

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-postgres.yml"))
                .withExposedService(
                    "postgres",
                    5432,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
        // .withLocalCompose(true) // docker-compose 가 설치되어 있어야 한다.
    }

    @Test
    fun `load postgres container`() {
        val postgresContainer: ContainerState by lazy {
            dockerComposeContainer.getContainerByServiceName("postgres").orElseThrow()
        }
        postgresContainer.isRunning.shouldBeTrue()

        val host = postgresContainer.host
        val port = postgresContainer.getMappedPort(5432)

        log.debug { "postgres host=$host, port=$$port" }
    }
}
