package io.bluetape4k.workshop.micrometer.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.micrometer.observation.coroutines.withObservationContext
import io.bluetape4k.workshop.micrometer.model.Todo
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingleOrNull
import net.datafaker.Faker
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class CoroutineService(
    private val webClientBuilder: WebClient.Builder,
    private val observationRegistry: ObservationRegistry,
) {

    companion object: KLogging() {
        private val faker = Faker()
    }

    private val client = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build()

    suspend fun getName(): String {
        log.debug { "Get fake name in coroutine service." }
        // pre-processing
        withObservationContext("pre-processing-get-name", observationRegistry) {
            log.debug { "pre-processing-get-name ..." }
            delay(100)
        }

        val name = withObservationContext("get-name-in-coroutine", observationRegistry) {
            log.debug { "Get fake name in coroutine service..." }
            delay(100)
            faker.name().fullName()
        }
        // post-processing
        withObservationContext("post-processing-get-name", observationRegistry) {
            log.debug { "psot-processing-get-name ..." }
            delay(100)
        }

        return name ?: "Unknown"
    }

    suspend fun getTodo(id: Int): Todo? {
        preProcessing()
        val todo = getTodoById(id)
        postProcessing()
        return todo
    }

    private suspend fun getTodoById(id: Int): Todo? {
        return withObservationContext("get-todo-by-id", observationRegistry) {
            log.debug { "Get todo by id[$id] in coroutine service." }
            delay(10)

            log.debug { "Get todo by id[$id] from jsonplaceholder..." }
            client.get()
                .uri("/todos/${id}")
                .retrieve()
                .bodyToMono<Todo>()
                .awaitSingleOrNull()
        }
    }

    private suspend fun preProcessing() {
        withObservationContext("pre-processing", observationRegistry) {
            log.debug { "Pre processing ..." }
            delay(200)
        }
    }

    private suspend fun postProcessing() {
        withObservationContext("post-processing", observationRegistry) {
            log.debug { "Post processing ..." }
            delay(300)
        }
    }
}
