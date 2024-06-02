package io.bluetape4k.workshop.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.workshop.AbstractProblemTest
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

class TaskControllerTest(
    @Autowired private val client: WebTestClient,
): AbstractProblemTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
    }

    @Test
    fun `get all tasks`() = runTest {
        val tasks = client.get()
            .uri("/tasks")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TaskController.Task>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        tasks.shouldNotBeEmpty()
        tasks.forEach {
            log.debug { it }
        }
    }

    @Test
    fun `get task by valid id`() = runTest {
        val task = client.get()
            .uri("/tasks/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<TaskController.Task>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        task.id shouldBeEqualTo 1L
    }

    /**
     * Get invalid task id
     *
     * Response:
     * ```json
     * {
     *     "title": "Bad Request",
     *     "status": 400,
     *     "detail": "400 BAD_REQUEST \"Type mismatch.\"; nested exception is org.springframework.beans.TypeMismatchException: Failed to convert value of type 'java.lang.String' to required type 'long'; nested exception is java.lang.NumberFormatException: For input string: \"abc\"",
     *     "cause": {
     *         "title": "Internal Server Error",
     *         "status": 500,
     *         "detail": "Failed to convert value of type 'java.lang.String' to required type 'long'; nested exception is java.lang.NumberFormatException: For input string: \"abc\"",
     *         "cause": {
     *             "title": "Internal Server Error",
     *             "status": 500,
     *             "detail": "For input string: \"abc\""
     *         }
     *     }
     * }
     * ```
     */
    @Test
    fun `get task with invalid format id`() {
        client.get()
            .uri("/tasks/abc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Bad Request")
            .consumeWith { result ->
                val body = result.responseBody!!
                log.debug { body.toUtf8String() }
            }
    }

    /**
     * Get task non-existing id
     *
     * ```json
     * {
     *     "title": "찾는 Task 없음",
     *     "status": 404,
     *     "detail": "TaskId[9999]에 해당하는 Task를 찾을 수 없습니다.",
     *     "instance": "/tasks/9999"
     * }
     * ```
     */
    @Test
    fun `get task non-existing id`() = runTest {
        client.get()
            .uri("/tasks/9999")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath(("$.detail")).isEqualTo("TaskId[9999]에 해당하는 Task를 찾을 수 없습니다.")
            .consumeWith { result ->
                val body = result.responseBody!!
                log.debug { body.toUtf8String() }
            }
    }

    /**
     * [UnsupportedOperationException] 이 발생하는 경우
     *
     * ```json
     * {
     *     "title": "Not Implemented",
     *     "status": 501,
     *     "detail": "구현 중",
     *     "cause": {
     *         "title": "Internal Server Error",
     *         "status": 500,
     *         "detail": "Boom!"
     *     }
     * }
     * ```
     */
    @Test
    fun `when call api which throw UnsupportedOperationException`() = runTest {
        client.put()
            .uri("/tasks/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
            .expectBody()
            .jsonPath("$.detail").isEqualTo("구현 중")
            .consumeWith { result ->
                val body = result.responseBody!!
                log.debug { body.toUtf8String() }
            }
    }

    /**
     * When call api which throw [AccessDeniedException]
     *
     * ```json
     * {
     *     "title": "Internal Server Error",
     *     "status": 500,
     *     "detail": "You can't delete this task [1]"
     * }
     * ```
     */
    @Test
    fun `when call api which throw AccessDeniedException`() = runTest {
        client.delete()
            .uri("/tasks/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectBody()
            .jsonPath("$.detail").isEqualTo("You can't delete this task [1]")
            .consumeWith { result ->
                val body = result.responseBody!!
                log.debug { body.toUtf8String() }
            }
    }
}
