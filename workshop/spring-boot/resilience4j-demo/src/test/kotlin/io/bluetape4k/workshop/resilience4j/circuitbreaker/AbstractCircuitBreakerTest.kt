package io.bluetape4k.workshop.resilience4j.circuitbreaker

import io.bluetape4k.workshop.resilience4j.AbstractIntegrationTest
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.amshove.kluent.shouldBeEqualTo

abstract class AbstractCircuitBreakerTest: AbstractIntegrationTest() {

    protected fun checkHealthStatus(circuitBreakerName: String, state: CircuitBreaker.State) {
        val circuitBreaker = circuirtBreakerRegistry.circuitBreaker(circuitBreakerName)
        circuitBreaker.state shouldBeEqualTo state
    }
}
