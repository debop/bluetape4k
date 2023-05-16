package io.bluetape4k.workshop.resilience4j

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
abstract class AbstractIntegrationTest {

    companion object: KLogging() {
        const val BACKEND_A = "backendA"
        const val BACKEND_B = "backendB"
    }

    @Autowired
    protected val circuirtBreakerRegistry: CircuitBreakerRegistry = uninitialized()

    @Autowired
    protected val restTemplate: TestRestTemplate = uninitialized()

    @Autowired
    protected val webClient: WebTestClient = uninitialized()

    @BeforeEach
    fun setup() {
        transitionToCloseState(BACKEND_A)
        transitionToCloseState(BACKEND_B)
    }

    protected fun transitionToOpenState(circuitBreakerName: String) {
        val circuitBreaker = circuirtBreakerRegistry.circuitBreaker(circuitBreakerName)
        circuitBreaker.transitionToOpenState()
    }

    protected fun transitionToCloseState(circuitBreakerName: String) {
        val circuitBreaker = circuirtBreakerRegistry.circuitBreaker(circuitBreakerName)
        circuitBreaker.transitionToClosedState()
    }


}
