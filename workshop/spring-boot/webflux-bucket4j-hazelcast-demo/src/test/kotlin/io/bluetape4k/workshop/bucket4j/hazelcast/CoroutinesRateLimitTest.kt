package io.bluetape4k.workshop.bucket4j.hazelcast

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CoroutinesRateLimitTest: AbstractRateLimitTest() {

    @Test
    fun `call hello with rate limit`() = runTest {
        val url = "/coroutines/hello"
        val limit = 5
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }

    @Test
    fun `call world with rate limit`() = runTest {
        val url = "/coroutines/world"
        val limit = 10
        repeat(limit) {
            successfulWebRequest(url, limit - 1 - it)
        }

        blockedWebRequestDueToRateLimit(url)
    }
}
