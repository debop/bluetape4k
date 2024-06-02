package io.bluetape4k.workshop.cloud.gateway

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.cloud.gateway.GatewayApplication
import io.bluetape4k.workshop.cloud.gateway.routes.HttpbinRoutes
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.test.ClassPathExclusions
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.util.TestSocketUtils
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.Duration

@ClassPathExclusions(
    "micrometer-core.jar",
    "spring-boot-actuator-*.jar",
    "spring-boot-actuator-autoconfigure-*.jar"
)
@DirtiesContext
class GatewayApplicationWithoutMetricsTest {

    companion object: KLogging() {

        private var port = TestSocketUtils.findAvailableTcpPort()

        init {
            System.setProperty("server.port", port.toString())
        }
    }

    private lateinit var baseUri: String
    private lateinit var client: WebTestClient

    @AfterAll
    fun afterAll() {
        System.clearProperty("server.port")
    }

    @BeforeEach
    fun beforeEach() {
        baseUri = "http://localhost:$port"
        client = WebTestClient.bindToServer()
            .responseTimeout(Duration.ofSeconds(10))
            .baseUrl(baseUri)
            .build()
    }

    private fun init(config: Class<*>): ConfigurableApplicationContext {
        return SpringApplicationBuilder().web(WebApplicationType.REACTIVE)
            .sources(GatewayApplication::class.java, config)
            .run()
    }

    @Test
    fun `actuator metrics`() {
        init(GatewayApplicationTest.TestConfig::class.java)

        client.get().uri("/get").exchange().expectStatus().isOk
        client.get()
            .uri("http://localhost:$port/actuator/metrics/spring.cloud.gateway.requests")
            .exchange()
            .expectStatus().isOk
            .expectBody<String>().isEqualTo(HttpbinRoutes.HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS)
    }
}
