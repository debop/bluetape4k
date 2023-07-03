package io.bluetape4k.workshop.bucket4j.hazelcast

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("webflux")
class ReactiveRateLimitTest(@Autowired private val client: WebTestClient) {

    @Test
    fun `call hello with rate limit`() = runTest {
        val url = "/reactive/hello"
        val limit = 5
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }

    @Test
    fun `call world with rate limit`() = runTest {
        val url = "/reactive/world"
        val limit = 10
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }

    private fun successfulWebRequest(url: String, remainingTries: Int) {
        client.get()
            .uri(url)
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals("X-Rate-Limit-Remaining", remainingTries.toString())
    }

    private fun blockedWebRequestDueToRateLimit(url: String) {
        client.get()
            .uri(url)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectBody().jsonPath("error", "Too many requests!")
    }
}
