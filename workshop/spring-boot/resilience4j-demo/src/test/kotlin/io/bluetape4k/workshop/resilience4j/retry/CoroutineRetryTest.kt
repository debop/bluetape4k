package io.bluetape4k.workshop.resilience4j.retry

import org.junit.jupiter.api.Test

class CoroutineRetryTest: AbstractRetryTest() {

    @Test
    fun `Backend A 는 retry 를 3회 시도한다`() {
        val current = getCurrentCount(FAILED_WITH_RETRY, BACKEND_A)

        procedureSuspendFailure(BACKEND_A)

        checkMetrics(FAILED_WITH_RETRY, BACKEND_A, current + 1)
    }

    @Test
    fun `Backend B 는 retry 를 3회 시도한다`() {
        val currentCount = getCurrentCount(FAILED_WITH_RETRY, BACKEND_B)

        procedureSuspendFailure(BACKEND_B)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(FAILED_WITH_RETRY, BACKEND_B, currentCount + 1)
    }

    @Test
    fun `Backend A 성공 호출은 retry 없이 실행된다`() {
        val currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_A)

        procedureSuspendSuccess(BACKEND_A)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_A, currentCount + 1)
    }

    @Test
    fun `Backend B 성공 호출은 retry 없이 실행된다`() {
        val currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_B)

        procedureSuspendSuccess(BACKEND_B)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_B, currentCount + 1)
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
}
