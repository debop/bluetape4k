package io.bluetape4k.workshop.kafka

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

/**
 * 테스트를 하려면 KafkaApplication 을 실행 한 후 테스트 코드를 실행하세요.
 *
 * @constructor Create empty Ping pong application test
 */
class PingPongApplicationTest {

    companion object: KLogging()

    val client = WebClient.builder().baseUrl("http://localhost:8080").build()

    @Disabled("KafkaApplication 을 수동으로 실행시켜야 합니다.")
    @Test
    fun `call ping and then return pong`() = runTest {
        client.get()
            .uri("/ping")
            .awaitExchange { response ->
                response.statusCode() shouldBeEqualTo HttpStatus.OK
                response.awaitBody<String>() shouldContain "pong"
            }
    }
}
