package io.bluetape4k.workshop.micrometer.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.micrometer.AbstractTracingTest
import io.bluetape4k.workshop.micrometer.model.Todo
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class CoroutineControllerTest(@Autowired private val client: WebTestClient): AbstractTracingTest() {

    companion object: KLogging()

    @Test
    fun `get name in coroutines`() = runTest {
        client.get()
            .uri("/coroutine/name")
            .exchange()
            .expectStatus().isOk
            .expectBody<String>().returnResult().responseBody.shouldNotBeNull().shouldNotBeEmpty()
    }

    @Test
    fun `get todo in coroutines`() = runTest {
        val id = 42
        val todo = client.get()
            .uri("/coroutine/todos/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody<Todo>().returnResult().responseBody

        log.debug { "todo: $todo" }
        todo.shouldNotBeNull()
        todo.id shouldBeEqualTo id
    }
}
