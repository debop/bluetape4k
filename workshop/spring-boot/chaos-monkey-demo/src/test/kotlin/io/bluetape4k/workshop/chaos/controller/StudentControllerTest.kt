package io.bluetape4k.workshop.chaos.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.chaos.AbstractChaosTest
import io.bluetape4k.workshop.chaos.model.Student
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest(@Autowired private val client: WebTestClient): AbstractChaosTest() {

    companion object: KLogging()

    @Test
    fun `find all students`() {
        val students = client.get()
            .uri("/students")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Student>()
            .returnResult()
            .responseBody!!

        log.debug { "all students" }
        students.forEach {
            log.debug { it }
        }
        students.shouldNotBeEmpty()
    }

    @Test
    fun `find by id`() {
        val studentId = 10001
        val student = client.get()
            .uri("/students/$studentId")
            .exchange()
            .expectStatus().isOk
            .expectBody<Student>()
            .returnResult()
            .responseBody!!

        student.id shouldBeEqualTo studentId
        log.debug { student }
    }
}
