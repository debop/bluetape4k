package io.bluetape4k.workshop.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.retry.MaxRetriesExceededException
import io.github.resilience4j.retry.Retry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resilience4j")
class Resilience4jController {

    companion object: KLogging()

    private val circuitBreaker = CircuitBreaker.ofDefaults("default")
    private val retry = Retry.ofDefaults("default")


    @GetMapping("/circuit-breaker-open")
    suspend fun circuitBreakerOpen(): String {

        circuitBreaker.transitionToOpenState()

        circuitBreaker.executeRunnable {
            log.info { "Circuit breaker is opened" }
        }
        return "OK"
    }

    @GetMapping("/retry")
    suspend fun retry(): String {
        repeat(retry.retryConfig.maxAttempts) {
            retry.executeRunnable {
                throw MaxRetriesExceededException.createMaxRetriesExceededException(retry)
            }
        }
        return "OK"
    }
}
