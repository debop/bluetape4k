package io.bluetape4k.workshop.resilience4j.retry

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.resilience4j.AbstractIntegrationTest
import io.github.resilience4j.retry.RetryRegistry
import org.amshove.kluent.shouldContain
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractRetryTest: AbstractIntegrationTest() {

    companion object: KLogging() {
        const val FAILED_WITH_RETRY = "failed_with_retry"
        const val SUCCESS_WITHOUT_RETRY = "successful_without_retry"
    }

    @Autowired
    private val retryRegistry: RetryRegistry = uninitialized()

    protected fun getCurrentCount(kind: String, backendName: String): Float {
        val metrics = retryRegistry.retry(backendName).metrics

        if (kind == FAILED_WITH_RETRY) {
            return metrics.numberOfFailedCallsWithRetryAttempt.toFloat()
        }
        if (kind == SUCCESS_WITHOUT_RETRY) {
            return metrics.numberOfSuccessfulCallsWithoutRetryAttempt.toFloat()
        }
        return 0F
    }

    protected fun checkMetrics(kind: String, backendName: String, count: Float) {
        webClient.get().uri("/actuator/prometheus")
            .exchange()
            .expectBody().consumeWith {
                val body = String(it.responseBody!!)
                body shouldContain getMetricName(kind, backendName) + count

            }
    }

    protected fun getMetricName(kind: String, backend: String): String? {
        return """resilience4j_retry_calls_total{application="resilience4j-demo",kind="$kind",name="$backend",} """
    }
}
