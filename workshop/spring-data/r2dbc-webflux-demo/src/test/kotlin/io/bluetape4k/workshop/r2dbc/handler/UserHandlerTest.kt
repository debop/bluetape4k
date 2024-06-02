package io.bluetape4k.workshop.r2dbc.handler

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.AbstractWebfluxR2dbcApplicationTest
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import io.bluetape4k.workshop.r2dbc.domain.toModel
import io.bluetape4k.workshop.r2dbc.service.UserService
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class UserHandlerTest: AbstractWebfluxR2dbcApplicationTest() {

    companion object: KLogging()

    private val service = mockk<UserService>(relaxUnitFun = true)
    private val request = mockk<ServerRequest>(relaxUnitFun = true)

    private val handler = UserHandler(service)

    @BeforeEach
    fun beforeEach() {
        clearMocks(service, request)
    }

    @Test
    fun `find all users`() = runTest {
        coEvery { service.findAll() } returns flowOf(createUser(id = 1), createUser(id = 2))

        val response = handler.findAll(request)
        response.statusCode() shouldBeEqualTo HttpStatus.OK
    }

    @Test
    fun `when user is not exists, returns empty flow`() = runTest {
        coEvery { service.findAll() } returns emptyFlow()

        val response = handler.findAll(request)
        response.statusCode() shouldBeEqualTo HttpStatus.OK
    }

    @Test
    fun `find by id return OK`() = runTest {
        coEvery { request.pathVariable("id") } returns "1"
        coEvery { service.findById(1) } returns createUser(1)


        val response = handler.findUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.OK
    }

    @Test
    fun `find by id non exists return NotFound`() = runTest {
        coEvery { request.pathVariable("id") } returns "-1"
        coEvery { service.findById(-1) } returns null

        val response = handler.findUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.NOT_FOUND
    }

    @Test
    fun `when path variable is not numeric returns BadRequest`() = runTest {
        coEvery { request.pathVariable("id") } returns "id"
        coEvery { service.findById(any()) } returns createUser(id = 1)

        val response = handler.findUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.BAD_REQUEST
        coVerify(exactly = 0) { service.findById(any()) }
    }

    @Test
    fun `add new user returns OK`() = runTest {
        coEvery { request.bodyToMono<UserDTO>() } returns createUserDTO().toMono()
        coEvery { service.addUser(any()) } answers {
            firstArg<UserDTO>().toModel().copy(id = 999)
        }

        val response = handler.addUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.CREATED
    }

    @Test
    fun `when invalid body to addUser returns BadRequest`() = runTest {
        coEvery { request.bodyToMono<UserDTO>() } returns Mono.empty()

        val response = handler.addUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.BAD_REQUEST
    }

    @Test
    fun `when error in saveUser returns InternalServerError`() = runTest {
        coEvery { service.addUser(any()) } returns null
        coEvery { request.bodyToMono<UserDTO>() } returns createUserDTO().toMono()

        val response = handler.addUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.INTERNAL_SERVER_ERROR
    }

    @Test
    fun `when update existing user returns OK`() = runTest {
        coEvery { request.pathVariable("id") } returns "2"
        coEvery { request.bodyToMono<UserDTO>() } returns createUserDTO().toMono()
        coEvery { service.updateUser(2, any()) } answers {
            secondArg<UserDTO>().toModel(firstArg<Int>())
        }

        val response = handler.updateUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.OK
    }

    @Test
    fun `when provide non numeric id to updateUser returns BadRequest`() = runTest {
        coEvery { request.pathVariable("id") } returns "NOT-NUMERIC"

        val response = handler.updateUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.BAD_REQUEST
    }

    @Test
    fun `when empty body to updateUser returns BadRequest`() = runTest {
        coEvery { request.pathVariable("id") } returns "2"
        coEvery { request.bodyToMono<UserDTO>() } returns Mono.empty()

        val response = handler.updateUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.BAD_REQUEST
    }

    @Test
    fun `when update non-existing user returns BadRequest`() = runTest {
        coEvery { request.pathVariable("id") } returns "2"
        coEvery { request.bodyToMono<UserDTO>() } returns createUserDTO().toMono()
        coEvery { service.updateUser(2, any()) } returns null

        val response = handler.updateUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.NOT_FOUND
    }

    @Test
    fun `when delete user is success returns NoContent`() = runTest {
        coEvery { request.pathVariable("id") } returns "2"
        coEvery { service.deleteUser(2) } returns true

        val response = handler.deleteUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.NO_CONTENT
    }

    @Test
    fun `when delete non-existing user returns NotFound`() = runTest {
        coEvery { request.pathVariable("id") } returns "999"
        coEvery { service.deleteUser(999) } returns false

        val response = handler.deleteUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.NOT_FOUND
    }

    @Test
    fun `when delete user with invalid id type returns BadRequest`() = runTest {
        coEvery { request.pathVariable("id") } returns "NON-NUMERIC"

        val response = handler.deleteUser(request)
        response.statusCode() shouldBeEqualTo HttpStatus.BAD_REQUEST
    }
}
