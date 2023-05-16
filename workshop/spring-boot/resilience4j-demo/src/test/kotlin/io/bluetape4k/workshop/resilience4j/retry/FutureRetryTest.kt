package io.bluetape4k.workshop.resilience4j.retry

import org.junit.jupiter.api.Test

class FutureRetryTest: AbstractRetryTest() {

    @Test
    fun `Backend A 는 retry 를 3회 시도한다`() {
        val currentCount = getCurrentCount(FAILED_WITH_RETRY, BACKEND_A)

        procedureFailure(BACKEND_A)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(FAILED_WITH_RETRY, BACKEND_A, currentCount + 1)
    }

    @Test
    fun `Backend B 는 retry 를 3회 시도한다`() {
        val currentCount = getCurrentCount(FAILED_WITH_RETRY, BACKEND_B)

        procedureFailure(BACKEND_B)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(FAILED_WITH_RETRY, BACKEND_B, currentCount + 1)
    }

    @Test
    fun `Backend A 성공 호출은 retry 없이 실행된다`() {
        val currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_A)

        procedureSuccess(BACKEND_A)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_A, currentCount + 1)
    }

    @Test
    fun `Backend B 성공 호출은 retry 없이 실행된다`() {
        val currentCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, BACKEND_B)

        procedureSuccess(BACKEND_B)

        // 3회 retry 후 실패를 반환한다
        checkMetrics(SUCCESS_WITHOUT_RETRY, BACKEND_B, currentCount + 1)
    }

    private fun procedureFailure(backendName: String) {
        webClient.get().uri("/$backendName/futureFailure")
            .exchange()
            .expectStatus().is5xxServerError
    }

    private fun procedureSuccess(backendName: String) {
        webClient.get().uri("/$backendName/futureSuccess")
            .exchange()
            .expectStatus().is2xxSuccessful
    }
}
