package io.bluetape4k.workshop.application.event.custom

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.output.CaptureOutput
import io.bluetape4k.junit5.output.OutputCapturer
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@CaptureOutput
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomEventPublisherTest(@Autowired private val client: WebTestClient) {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
    }

    @Test
    fun `publish custom event`(output: OutputCapturer) = runSuspendWithIO {
        client.get()
            .uri("/event?message=CustomEventMessage")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<String>()
            .consumeWith { result ->
                result.responseBody shouldBeEqualTo "Finished"
            }

        delay(100L)
        output.capture() shouldContain "Handle custom event"
    }
}
