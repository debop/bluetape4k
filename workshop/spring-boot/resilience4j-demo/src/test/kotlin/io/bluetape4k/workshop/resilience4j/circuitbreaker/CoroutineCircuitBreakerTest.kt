package io.bluetape4k.workshop.resilience4j.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CoroutineCircuitBreakerTest: AbstractCircuitBreakerTest() {

    @Test
    fun `Backend A Suspend - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(2) {
            procedureSuspendFailure(BACKEND_A)
        }
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend A Suspend - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_A)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState()

        repeat(4) {
            procedureSuspendSuccess(BACKEND_A)
        }

        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend B Suspend - 연속된 예외에 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(4) {
            procedureSuspendFailure(BACKEND_B)
        }
        checkHealthStatus(BACKEND_B, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend B Suspend - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_B)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_B).transitionToHalfOpenState()

        repeat(5) {
            procedureSuspendSuccess(BACKEND_B)
        }

        checkHealthStatus(BACKEND_B, CircuitBreaker.State.CLOSED)
    }

    @Disabled("Flow 에 대해서는 @CircuitBreaker 가 적용되지 않는다")
    @Test
    fun `Backend A Flow - 예외가 누적되면 Circuit Breaker가 Open 됩니다`() {
        repeat(2) {
            procedureFlowFailure(BACKEND_A)
        }
        checkHealthStatus(BACKEND_A, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend A Flow - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_A)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_A).transitionToHalfOpenState()

        repeat(4) {
            procedureFlowSuccess(BACKEND_A)
        }

        checkHealthStatus(BACKEND_A, CircuitBreaker.State.CLOSED)
    }

    @Test
    fun `Backend B Flow - 예외가 누적되면 CircuitBreaker 가 OPEN 됩니다`() {
        repeat(4) {
            procedureFlowFailure(BACKEND_B)
        }
        checkHealthStatus(BACKEND_B, CircuitBreaker.State.OPEN)
    }

    @Test
    fun `Backend B Flow - 연속적으로 작업이 성공하면 Circuit Breaker 는 CLOSE 되어 있어야 한다`() {
        // OPEN 상태에서는 HALF OPEN 상태로 만든 후에야 CLOSE 상태로 돌아갈 수 있다
        // 먼저 Circuit Breaker 를 Half Open 상태로 만든다
        transitionToOpenState(BACKEND_B)
        circuirtBreakerRegistry.circuitBreaker(BACKEND_B).transitionToHalfOpenState()

        repeat(5) {
            procedureFlowSuccess(BACKEND_B)
        }

        checkHealthStatus(BACKEND_B, CircuitBreaker.State.CLOSED)
    }


    private fun procedureSuspendFailure(backendName: String) {
        webClient.get().uri("/coroutine/$backendName/suspendFailure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureSuspendSuccess(backendName: String) {
        webClient.get().uri("/coroutine/$backendName/suspendSuccess")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    private fun procedureFlowFailure(backendName: String) {
        webClient.get().uri("/coroutine/$backendName/flowFailure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureFlowSuccess(backendName: String) {
        webClient.get().uri("/coroutine/$backendName/flowSuccess")
            .exchange()
            .expectStatus().is2xxSuccessful
    }
}
