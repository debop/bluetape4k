package io.bluetape4k.workshop.r2dbc.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.workshop.r2dbc.AbstractWebfluxR2dbcApplicationTest
import io.bluetape4k.workshop.r2dbc.domain.User
import io.bluetape4k.workshop.r2dbc.domain.toDto
import io.bluetape4k.workshop.r2dbc.service.UserService
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

class UserControllerTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val service: UserService,
): AbstractWebfluxR2dbcApplicationTest() {

    companion object: KLogging()

    @Nested
    inner class Find {
        @Test
        fun `find all users as Flow`() = runTest {
            val users = client.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<User>()
                .returnResult()
                .responseBody
                .shouldNotBeNull()

            users.shouldNotBeEmpty()
            users.forEach { log.debug { it } }
        }

        @Test
        fun `find by id - existing user`() = runTest {
            client.get()
                .uri("/api/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<User>()
                .consumeWith { result ->
                    val user = result.responseBody.shouldNotBeNull()
                    log.debug { "Find by id[1] = $user" }
                }
        }

        @Test
        fun `find by id - non-existing user`() = runTest {
            client.get()
                .uri("/api/users/9999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .consumeWith { result ->
                    val message = result.responseBody?.toUtf8String()
                    log.debug { message }
                    message.shouldNotBeNull() shouldContain "Not Found"
                }
        }

        @Test
        fun `find by id - non-numeric id`() = runTest {
            client.get()
                .uri("/api/users/abc")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .consumeWith { result ->
                    val message = result.responseBody?.toUtf8String()
                    log.debug { message }
                    message.shouldNotBeNull() shouldContain "Bad Request"
                }
        }
    }

    @Nested
    inner class Search {
        @Test
        fun `search by valid email returns Users`() = runTest {
            val searchEmail = "user2@users.com"

            val searchedUsers = client.get()
                .uri("/api/users/search?email=$searchEmail")
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBodyList<User>()
                .hasSize(1)
                .returnResult()
                .responseBody
                .shouldNotBeNull()

            searchedUsers.all { it.email == searchEmail }.shouldBeTrue()
        }

        @Test
        fun `search by empty email returns Users`() = runTest {
            val searchEmail = ""

            client.get()
                .uri("/api/users/search?email=$searchEmail")
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `search without email returns Users`() = runTest {
            client.get()
                .uri("/api/users/search")
                .exchange()
                .expectStatus().isBadRequest
        }
    }

    @Nested
    inner class Add {
        @Test
        fun `add new user`() = runTest {
            val newUser = createUserDTO()

            val savedUser = client.post()
                .uri("/api/users")
                .bodyValue(newUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<User>()
                .returnResult()
                .responseBody.shouldNotBeNull()

            savedUser.id.shouldNotBeNull()
            savedUser.toDto() shouldBeEqualTo newUser
        }

        @Test
        fun `add new user with invalid format`() = runTest {
            val newUser = "new user"

            client.post()
                .uri("/api/users")
                .bodyValue(newUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `update existing user`() = runTest {
            val newUser = createUserDTO()
            val savedUser = service.addUser(newUser)!!

            val userToUpdate = createUserDTO()

            val updatedUser = client.put()
                .uri("/api/users/${savedUser.id}")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<User>()
                .returnResult()
                .responseBody.shouldNotBeNull()

            updatedUser.id.shouldNotBeNull()
            updatedUser.toDto() shouldBeEqualTo userToUpdate
        }

        @Test
        fun `update non-existing user`() = runTest {
            val userToUpdate = createUserDTO()

            client.put()
                .uri("/api/users/9999")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `update user with invalid format`() = runTest {
            val userToUpdate = "new user"

            client.put()
                .uri("/api/users/2")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }

        @Test
        fun `update user with invalid id`() = runTest {
            val userToUpdate = "new user"

            client.put()
                .uri("/api/users/abc")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `delete existing user`() = runTest {
            val newUser = createUserDTO()
            val savedUser = service.addUser(newUser)!!

            client.delete()
                .uri("/api/users/${savedUser.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<Boolean>()
                .returnResult()
                .responseBody.shouldNotBeNull().shouldBeTrue()
        }

        @Test
        fun `delete non-existing user`() = runTest {
            client.delete()
                .uri("/api/users/9999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `delete by non-numeric id`() = runTest {
            client.delete()
                .uri("/api/users/abc")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
        }
    }
}
