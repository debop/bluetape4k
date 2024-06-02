package io.bluetape4k.workshop.bucket4j.controller

import io.bluetape4k.codec.Base58
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.bucket4j.AbstractRateLimiterApplicationTest
import io.bluetape4k.workshop.bucket4j.utils.HeaderUtils
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

class ReactiveControllerTest(
    @Autowired private val client: WebTestClient,
): AbstractRateLimiterApplicationTest() {

    companion object: KLogging() {
        private const val PATH_V1 = "/api/v1/reactive/hello"      // RateLimit이 걸려 있음
        private const val PATH_V2 = "/api/v2/reactive/hello"      // RateLimit이 걸려 있지 않음
        private const val LIMIT_COUNT = 5   // RateLimit요 WebFilter가 2개라서 
    }

    @Test
    fun `사용자 ID 기분으로 RateLimit이 적용됩니다`() = runTest {
        val userId = "coroutines.debop." + Base58.randomString(6)

        // 10초 동안 10번으로 Rate Limit이 걸려 있음
        // WebFilter 2개가 걸려있어서, 5번만 호출해도 10개의 Token이 소비됩니다.
        repeat(LIMIT_COUNT) {
            client.get()
                .uri(PATH_V1)
                .header(HeaderUtils.X_BLUETAPE4K_UID, userId)
                .exchange()
                .expectStatus().isOk
                .expectHeader()
                .valueEquals(HeaderUtils.X_BLUETAPE4K_REMAINING_TOKEN, (2 * (LIMIT_COUNT - it - 1)).toString())
                .returnResult<String>().responseBody
                .awaitSingle() shouldContain "Hello World V1"
        }

        // 모든 Token이 소비되었으므로, TOO_MANY_REQUESTS가 발생합니다.
        client.get()
            .uri(PATH_V1)
            .header(HeaderUtils.X_BLUETAPE4K_UID, userId)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

    @Disabled("IP 로 Rate Limit을 거는 것은 /coroutines/v1, /reactive/v1 둘 다 걸려서 동시에 테스트 할 수 없습니다.")
    @Test
    fun `사용자 ID가 없으면 IP Address 기준으로 RateLimit이 적용됩니다`() = runTest {
        // 10초 동안 10번으로 Rate Limit이 걸려 있음
        // WebFilter 2개가 걸려있어서, 5번만 호출해도 10개의 Token이 소비됩니다.
        repeat(LIMIT_COUNT) {
            client.get()
                .uri(PATH_V1)
                .exchange()
                .expectStatus().isOk
                .expectHeader()
                .valueEquals(HeaderUtils.X_BLUETAPE4K_REMAINING_TOKEN, (2 * (LIMIT_COUNT - it - 1)).toString())
                .returnResult<String>().responseBody
                .awaitSingle() shouldContain "Hello World V1"
        }

        // 모든 Token이 소비되었으므로, TOO_MANY_REQUESTS가 발생합니다.
        client.get()
            .uri(PATH_V1)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

    @Test
    fun `RateLimit에 적용되지 않은 경로에는 제한이 없습니다`() = runTest {
        val userId = "coroutines.debop." + Base58.randomString(6)

        // 10초 동안 10번으로 Rate Limit이 걸려 있음
        // WebFilter 2개가 걸려있어서, 5번만 호출해도 10개의 Token이 소비됩니다.
        repeat(LIMIT_COUNT) {
            client.get()
                .uri(PATH_V2)
                .header(HeaderUtils.X_BLUETAPE4K_UID, userId)
                .exchange()
                .expectStatus().isOk
                .returnResult<String>().responseBody
                .awaitSingle() shouldContain "Hello World V2"
        }

        client.get()
            .uri(PATH_V2)
            .header(HeaderUtils.X_BLUETAPE4K_UID, userId)
            .exchange()
            .expectStatus().isOk
    }
}
