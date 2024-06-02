package io.bluetape4k.workshop.webflux

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.webflux.model.Event
import io.bluetape4k.workshop.webflux.model.Quote
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBodyList
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleApplicationTest(
    @Autowired private val client: WebTestClient,
) {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        // check context loading 
    }

    @Test
    fun `get quotes`() = runSuspendWithIO {
        client.get()
            .uri("/quotes")
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().contentType(MediaType.APPLICATION_NDJSON)
            .expectBodyList<Event>()
            .consumeWith<ListBodySpec<Event>> { result ->
                val events = result.responseBody!!
                events.all { event ->
                    log.debug { "received event=$event" }
                    event.data.all { it.price > BigDecimal.ZERO }
                }.shouldBeTrue()
            }
    }

    @Test
    fun `fetch quotes by flow`() = runSuspendWithIO {
        client.get()
            .uri("/quotes/200")
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().contentType(MediaType.APPLICATION_NDJSON)
            .expectBodyList<Quote>()
            .consumeWith<ListBodySpec<Quote>> { result ->
                val quotes = result.responseBody!!
                quotes.all { quote ->
                    log.debug { "received quote=$quote" }
                    quote.price > BigDecimal.ZERO
                }.shouldBeTrue()
            }
    }
}
