package io.bluetape4k.workshop.cloud.gateway

import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.gateway.config.GatewayMetricsProperties
import org.springframework.cloud.gateway.test.HttpBinCompatibleController
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.test.util.TestSocketUtils
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.io.IOException
import java.time.Duration

@SpringBootTest(
    classes = [GatewayApplicationTest.TestConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "management.endpoint.gateway.enabled=true",
        "management.server.port=\${test.port}",
    ]
)
class GatewayApplicationTest: AbstractGatewayTest() {

    companion object: KLogging() {
        private const val TEST_HEADER = "X-TestHeader"

        private var managementPort: String

        init {
            managementPort = TestSocketUtils.findAvailableTcpPort().toString()
            log.info { "management port=$managementPort" }
            System.setProperty("test.port", managementPort)
        }
    }

    @Autowired
    private lateinit var metricsProperties: GatewayMetricsProperties

    @LocalServerPort
    lateinit var port: String

    private lateinit var client: WebTestClient
    private lateinit var baseUri: String

    @AfterAll
    fun afterAll() {
        System.clearProperty("test.port")
    }

    @BeforeEach
    fun beforeEach() {
        baseUri = "http://localhost:$port"
        log.info { "baseUri=$baseUri" }
        client = WebTestClient.bindToServer()
            .responseTimeout(Duration.ofSeconds(10))
            .baseUrl(baseUri)
            .build()
    }

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
        client.get().uri("/get").exchange().expectStatus().isOk
    }

    @Test
    fun `read body predicate string works`() {
        client.post()
            .uri("/post")
            .header("Host", "www.readbody.org")
            .bodyValue("hi")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "read_body_pred")
            .expectBody<Map<*, *>>()
            .consumeWith { result ->
                result.responseBody!!["data"] shouldBeEqualTo "hi"
            }
    }

    @Test
    fun `rewrite request body string works`() {
        client.post()
            .uri("/post")
            .header("Host", "www.rewriterequestupper.org")
            .bodyValue("hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_request_upper")
            .expectBody<Map<*, *>>()
            .consumeWith { result ->
                result.responseBody!!["data"] shouldBeEqualTo "HELLOHELLO"
            }
    }

    @Test
    fun `rewrite request body object works`() {
        client.post()
            .uri("/post")
            .header("Host", "www.rewriterequestobj.org")
            .bodyValue("hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_request")
            .expectBody<Map<*, *>>()
            .consumeWith { result ->
                result.responseBody!!["data"] shouldBeEqualTo """{"message":"HELLO"}"""
            }
    }

    @Test
    fun `rewrite response body string works`() {
        client.post()
            .uri("/post")
            .header("Host", "www.rewriteresponseupper.org")
            .bodyValue("hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_response_upper")
            .expectBody<Map<*, *>>()
            .consumeWith { result ->
                // body의 모든 key, value 가 uppercase 이다.
                log.debug { "body=${result.responseBody!!.entries.joinToString()}" }
                result.responseBody!!["DATA"] shouldBeEqualTo "HELLO"
            }
    }

    @Test
    fun `rewrite response empty body to string works`() {
        client.post()
            .uri("/post/empty")
            .header("Host", "www.rewriteemptyresponse.org")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_empty_response")
            .expectBody<String>()
            .consumeWith { result ->
                val body = result.responseBody!!
                body shouldBeEqualTo "emptybody"
            }
    }

    @Test
    fun `empty body supplier not called when body present`() {
        client.post()
            .uri("/post")
            .header("Host", "www.rewriteresponsewithfailsupplier.org")
            .bodyValue("hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_respnose_fail_supplier")
            .expectBody<Map<*, *>>()
            .consumeWith { result ->
                // body의 모든 key, value 가 uppercase 이다.
                log.debug { "body=${result.responseBody!!.entries.joinToString()}" }
                result.responseBody!!["DATA"] shouldBeEqualTo "HELLO"
            }
    }

    @Test
    fun `rewrite response body object works`() {
        client.post()
            .uri("/post")
            .header("Host", "www.rewriteresponseobj.org")
            .bodyValue("hello")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "rewrite_response_obj")
            .expectBody<String>()
            .consumeWith { result ->
                result.responseBody shouldBeEqualTo "hello"
            }
    }

    @Test
    fun `comprex predicate`() {
        client.get()
            .uri("/anything/png")
            .header("Host", "www.abc.org")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "foobar")
    }

    @Test
    fun `route from Kotlin`() {
        client.get()
            .uri("/anything/kotlinroute")
            .header("Host", "kotlin.abc.org")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(TEST_HEADER, "foobar")
    }

    @Test
    fun `actuator managment port`() {
        client.get()
            .uri("http://localhost:$managementPort/actuator/gateway/routes")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `actuator metrics`() {
        `context loading`()
        val metricName = metricsProperties.prefix + ".requests"

        client.get()
            .uri("http://localhost:$managementPort/actuator/metrics/$metricName")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { result ->
                val body = result.responseBodyContent!!.toUtf8String()
                val mapper = Jackson.defaultJsonMapper

                try {
                    val actualObj = mapper.readTree(body)
                    log.debug { "body=${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualObj)}" }

                    val findValuye = actualObj.findValue("name")
                    findValuye.asText() shouldBeEqualTo metricName
                } catch (e: IOException) {
                    throw IllegalStateException(e)
                }
            }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @LoadBalancerClient(name = "httpbin", configuration = [LoadBalancerConfig::class])
    @Import(GatewayApplication::class)
    internal class TestConfig {

        /**
         * [httpbin.org](https://httpbin.org) REST API 와 호환되도록 하는 Rest Controller 입니다.
         *
         * Dependency 에 다음을 추가해야 합니다.
         *
         * ```kotlin
         * testImplementation(Libs.springCloud("gateway-server") + "::tests")
         * ```
         */
        @Bean
        fun httpBinCompatibleController(): HttpBinCompatibleController =
            HttpBinCompatibleController()

    }

    internal class LoadBalancerConfig {

        @LocalServerPort
        private val port: Int = 0

        @Bean
        fun fixedServiceInstanceListSupplier(env: Environment): ServiceInstanceListSupplier {
            log.debug { "LocalServerPort=$port" }
            return ServiceInstanceListSuppliers.from(
                "httpbin",
                DefaultServiceInstance("httpbin-1", "httpbin", "localhost", port, false)
            )
        }
    }
}
