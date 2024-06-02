package io.bluetape4k.workshop.security.server.application.login

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.security.server.ApplicationTest
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders

internal class LoginIntegrationTest: ApplicationTest() {

    @Value("\${app.first_user.username}")
    private lateinit var firstUsername: String

    @Value("\${app.first_user.password}")
    private lateinit var firstPassword: String

    @Test
    fun `가입된 사용자가 로그인한다면 access 와 refresh token을 받는다`() {
        val responseHeaders: HttpHeaders = client
            .post().uri("/login")
            .bodyValue(LoginRequest(firstUsername, firstPassword))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().exists("Authorization")
            .expectHeader().exists("JWT-Refresh-Token")
            .expectBody()
            .returnResult()
            .responseHeaders

        val accessToken = responseHeaders.getFirst("Authorization")!!
        val refreshToken = responseHeaders.getFirst("JWT-Refresh-Token")!!

        val decodedAccessToken = jwtService.decodeAccessToken(accessToken)
        val decodedRefreshToken = jwtService.decodeRefreshToken(refreshToken)

        log.debug { "access token=$decodedAccessToken, refresh token=$decodedRefreshToken" }

        jwtService.getRoles(decodedAccessToken).any { it.authority == "ROLE_USER" }.shouldBeTrue()
    }

    @Test
    fun `가입안된 사용자가 로그인하려고 하면 UNAUTHORIZED 에러가 발생한다`() = runSuspendWithIO {
        client
            .post().uri("/login")
            .bodyValue(LoginRequest("unknown@example.com", "unknownpassword"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `잘못된 형식의 이메일 정보를 전송하면 BAD REQUEST 에러가 발생한다`() = runSuspendWithIO {
        client
            .post().uri("/login")
            .bodyValue(LoginRequest("invalid@asd", "invalid"))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `잘못된 길이(8~256)의 Password 정보를 전송하면 BAD REQUEST 에러가 발생한다`() = runSuspendWithIO {
        client
            .post().uri("/login")
            .bodyValue(LoginRequest("invalid@asd.com", "pwd"))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `가입자가 비밀번호가 틀린 경우 UNAUTHORIZED 에러가 발생한다`() = runSuspendWithIO {
        client
            .post().uri("/login")
            .bodyValue(LoginRequest(firstUsername, "invalidpassword"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `잘못된 포맷의 Log 정보를 전송하면 BAD REQUEST 에러가 발생한다`() = runSuspendWithIO {
        val badRequest = object {
            val invalidUsername = "invalid@examplec.com"
            val password = "invalid"
        }
        client
            .post().uri("/login")
            .bodyValue(badRequest)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET 방식으로 로그인 시에는 NOT FOUND 예외가 발생한다`() {
        client
            .get().uri("/login")
            .exchange()
            .expectStatus().isNotFound
    }
}
