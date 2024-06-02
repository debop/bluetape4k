package io.bluetape4k.workshop.bucket4j

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ReactiveRateLimitTest: AbstractRateLimitTest() {

    @Test
    fun `call reactive hello with rate limit`() = runTest {
        val url = "/reactive/hello"
        val limit = 5
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }

    @Test
    fun `call reactive world with rate limit`() = runTest {
        val url = "/reactive/world"
        val limit = 10
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }
}
