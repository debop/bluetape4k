package io.bluetape4k.workshop.webflux.config

import io.bluetape4k.workshop.webflux.service.QuoteGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.time.Duration

@Configuration
class SampleWebfluxRouter(
    @Value("classpath:/static/index.html")
    private val indexHtml: Resource,
) {

    @Bean
    fun getIndex(): RouterFunction<ServerResponse> = coRouter {
        GET("/") { request ->
            ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValueAndAwait(indexHtml)
        }
    }

    @Bean
    fun compositeRountes(quoteGenerator: QuoteGenerator): RouterFunction<ServerResponse> = coRouter {
        GET("/quotes") { request ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .bodyAndAwait(quoteGenerator.getQuotes())
        }
        GET("/quotes/{duration}") { request ->
            val duration = Duration.ofMillis(request.pathVariable("duration").toLong())
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .bodyAndAwait(quoteGenerator.fetchQuoteAsFlow(duration))
        }
    }
}
