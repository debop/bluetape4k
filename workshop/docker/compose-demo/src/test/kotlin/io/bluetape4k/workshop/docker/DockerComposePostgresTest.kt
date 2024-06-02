package io.bluetape4k.workshop.docker

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.ShutdownQueue
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
 * Docker Compose Module을 이용하여 Docker Compose 파일에 정의된 서비스를 실행합니다.
 *
 * 참고 : [Docker Compose Module](https://www.testcontainers.org/modules/docker_compose/)
 */
@Testcontainers
class DockerComposePostgresTest {

    companion object: KLogging() {

        private const val POSTGRES_NAME = "postgres"
        private const val POSTGRES_PORT = 5432

        @JvmStatic
        @Container
        private val dockerComposeContainer: DockerComposeContainer<*> =
            DockerComposeContainer(File("src/test/resources/docker/docker-compose-postgres.yml"))
                .withExposedService(
                    POSTGRES_NAME,
                    POSTGRES_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .apply {
                    ShutdownQueue.register(this)
                }
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
