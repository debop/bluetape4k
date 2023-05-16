package io.bluetape4k.workshop.resilience4j.circuitbreaker

import io.bluetape4k.logging.KLogging
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.junit.jupiter.api.Test

class CircuitBreakerTest: AbstractCircuitBreakerTest() {

    companion object: KLogging()

    @Test
    fun `Backend A - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(2) {
            procedureFailure(BACKEND_A)
        }
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend A - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_A)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState()

        repeat(3) {
            procedureSuccess(BACKEND_A)
        }

        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend B - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(4) {
            procedureFailure(BACKEND_B)
        }
        checkHealthStatus(BACKEND_B, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend B - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_B)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_B).transitionToHalfOpenState()

        repeat(3) {
            procedureSuccess(BACKEND_B)
        }

        checkHealthStatus(BACKEND_B, CircuitBreaker.State.CLOSED)
    }

    private fun procedureFailure(backendName: String) {
        webClient.get()
            .uri("/$backendName/failure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureSuccess(backendName: String) {
        webClient.get()
            .uri("/$backendName/success")
            .exchange()
            .expectStatus().isOk
    }
}
