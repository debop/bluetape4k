package io.bluetape4k.workshop.r2dbc.handler

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.r2dbc.AbstractWebfluxR2dbcApplicationTest
import io.bluetape4k.workshop.r2dbc.domain.User
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import io.bluetape4k.workshop.r2dbc.domain.toDto
import io.bluetape4k.workshop.r2dbc.service.UserService
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

class UserHandlerIT(
    @Autowired private val client: WebTestClient,
    @Autowired private val service: UserService,
): AbstractWebfluxR2dbcApplicationTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
    }

    @Nested
    inner class Find {
        @Test
        fun `find all users`() = runTest {
            val response = client.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBodyList<UserDTO>()
                .returnResult()

            response.shouldNotBeNull()
            val users = response.responseBody.shouldNotBeNull()
            users.forEach { user ->
                log.debug { "findAll. user=$user" }
            }
        }

        @Test
        fun `find by id - exsting user`() = runTest {
            client.get()
                .uri("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<User>()
                .consumeWith { result ->
                    val user = result.responseBody.shouldNotBeNull()
                    log.debug { "Find by Id[1] =$user" }
                }
        }

        @Test
        fun `find by id - non-exsting user`() = runTest {
            client.get()
                .uri("/users/9999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `find by id - invalid user id`() = runTest {
            client.get()
                .uri("/users/user_id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("`id` must be numeric")
        }
    }

    @Nested
    inner class Search {
        @Test
        fun `search by email`() = runTest {
            val searchEmail = "user2@users.com"

            val searchedUsers = client.get()
                .uri("/users/search?email=$searchEmail")
                .accept(MediaType.APPLICATION_JSON)
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
        fun `search by empty email returns BadReqeust`() = runTest {
            val searchEmail = ""
            client.get()
                .uri("/users/search?email=$searchEmail")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Not provide email to search")
        }

        @Test
        fun `search without params return BadRequest`() = runTest {
            client.get()
                .uri("/users/search")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Search must have query parameter")
        }
    }

    @Nested
    inner class Add {
        @Test
        fun `add new user`() = runTest {
            val newUser = createUserDTO()

            val savedUser = client.post()
                .uri("/users")
                .bodyValue(newUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated
                .expectBody<User>()
                .returnResult()
                .responseBody
                .shouldNotBeNull()

            savedUser.id.shouldNotBeNull()
            savedUser.toDto() shouldBeEqualTo newUser
        }

        @Test
        fun `add new user with bad format`() = runTest {
            val newUser = "bad format"

            client.post()
                .uri("/users")
                .bodyValue(newUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid body")
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
                .uri("/users/${savedUser.id}")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody<User>()
                .returnResult()
                .responseBody
                .shouldNotBeNull()

            updatedUser.toDto() shouldBeEqualTo userToUpdate
        }

        @Test
        fun `update with non-numeric id`() = runTest {
            val userToUpdate = createUserDTO()

            client.put()
                .uri("/users/abc")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("`id` must be numeric")
        }

        @Test
        fun `update with invalid userDTO`() = runTest {
            val userToUpdate = "bad format"

            client.put()
                .uri("/users/1")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid body")
        }


        @Test
        fun `update non-existing user`() = runTest {
            val userToUpdate = createUserDTO()

            client.put()
                .uri("/users/9999")
                .bodyValue(userToUpdate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("User[9999] not found")
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `delete existing user`() = runTest {
            val newUser = createUserDTO()
            val savedUser = service.addUser(newUser)!!

            client.delete()
                .uri("/users/${savedUser.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent
        }

        @Test
        fun `delete non-existing user`() = runTest {
            client.delete()
                .uri("/users/9999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("User[9999] not found")
        }

        @Test
        fun `delete user with non-numeric id`() = runTest {
            client.delete()
                .uri("/users/abc")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.message").isEqualTo("`id` must be numeric")
        }
    }
}
