package io.bluetape4k.workshop.micrometer.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.micrometer.model.Todo
import io.micrometer.observation.annotation.Observed
import net.datafaker.Faker
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Service
@Observed
class ReactorService(
    private val webClientBuilder: WebClient.Builder,
) {
    companion object: KLogging() {
        val faker = Faker()
    }

    private val client = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build()

    fun getName(): Mono<String> {
        return Mono.just(faker.name().fullName())
            .doOnEach {
                log.debug { "Get fake name in reactor service." }
            }
            .delayElement(Duration.ofMillis(100))
    }

    fun getTodoById(id: Int): Mono<Todo> {
        return client.get()
            .uri("/todos/${id}")
            .retrieve()
            .bodyToMono<Todo>()
            .doOnEach { log.debug { "Get todo by id[$id] in reactor service." } }
            .delayElement(Duration.ofMillis(100))
    }

}
