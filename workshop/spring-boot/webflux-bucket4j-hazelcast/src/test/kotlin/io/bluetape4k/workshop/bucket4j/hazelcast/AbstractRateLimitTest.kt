package io.bluetape4k.workshop.bucket4j.hazelcast

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("webflux")
abstract class AbstractRateLimitTest {
    companion object: KLogging()

    @Autowired
    protected val client: WebTestClient = uninitialized()

    protected fun successfulWebRequest(url: String, remainingTries: Int) {
        client.get()
            .uri(url)
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("X-Rate-Limit-Remaining", remainingTries.toString())
    }

    protected fun blockedWebRequestDueToRateLimit(url: String) {
        client.get()
            .uri(url)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectBody().jsonPath("error", "Too many requests!")
    }
}
