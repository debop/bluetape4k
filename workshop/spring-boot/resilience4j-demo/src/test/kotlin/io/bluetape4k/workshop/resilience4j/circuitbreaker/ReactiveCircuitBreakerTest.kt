package io.bluetape4k.workshop.resilience4j.circuitbreaker

import io.bluetape4k.workshop.resilience4j.AbstractIntegrationTest
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.junit.jupiter.api.Test

class ReactiveCircuitBreakerTest: AbstractCircuitBreakerTest() {

    @Test
    fun `Backend A Mono - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(2) {
            procedureMonoFailure(BACKEND_A)
        }
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend A Mono - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_A)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState()

        repeat(4) {
            procedureMonoSuccess(BACKEND_A)
        }

        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend B Mono - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(4) {
            procedureMonoFailure(AbstractIntegrationTest.BACKEND_B)
        }
        checkHealthStatus(AbstractIntegrationTest.BACKEND_B, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend B Mono - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(AbstractIntegrationTest.BACKEND_B)
        circuirtBreakerRegistry.circuitBreaker(AbstractIntegrationTest.BACKEND_B).transitionToHalfOpenState()

        repeat(5) {
            procedureMonoSuccess(AbstractIntegrationTest.BACKEND_B)
        }

        checkHealthStatus(AbstractIntegrationTest.BACKEND_B, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend A Flux - 예외가 누적되면 Circuit Breaker가 Open 됩니다`() {
        repeat(2) {
            procedureFluxFailure(BACKEND_A)
        }
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend A Flux - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_A)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState()

        repeat(4) {
            procedureFluxSuccess(BACKEND_A)
        }

        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend B Flux - 예외가 누적되면 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(4) {
            procedureFluxFailure(AbstractIntegrationTest.BACKEND_B)
        }
        checkHealthStatus(AbstractIntegrationTest.BACKEND_B, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend B - Flux 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(AbstractIntegrationTest.BACKEND_B)
        circuirtBreakerRegistry.circuitBreaker(AbstractIntegrationTest.BACKEND_B).transitionToHalfOpenState()

        repeat(5) {
            procedureFluxSuccess(AbstractIntegrationTest.BACKEND_B)
        }

        checkHealthStatus(AbstractIntegrationTest.BACKEND_B, CircuitBreaker.State.CLOSED)
    }

    private fun procedureMonoFailure(backendName: String) {
        webClient.get().uri("/$backendName/monoFailure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureMonoSuccess(backendName: String) {
        webClient.get().uri("/$backendName/monoSuccess")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    private fun procedureFluxFailure(backendName: String) {
        webClient.get().uri("/$backendName/fluxFailure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureFluxSuccess(backendName: String) {
        webClient.get().uri("/$backendName/fluxSuccess")
            .exchange()
            .expectStatus().is2xxSuccessful
    }
}
