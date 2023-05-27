package io.bluetape4k.workshop.security.jwt.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HelloControllerTest(@Autowired private val client: WebTestClient) {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
    }

    @Disabled("이렇게 plain password 를 전달하면, password encoder 설정을 막아야 하기 때문에 @WithMockUser 를 사용하는 것을 추천합니다")
    @Test
    fun `인증된 user가 새로운 토큰 발급을 요청하면 발급되고 인증되어야 한다`() {
        // 인증 정보로 토큰을 발급 받는다 
        val token = client.post()
            .uri("/token")
            .headers {
                it.setBasicAuth("user", "password")
            }
            .exchange()
            .expectStatus().isOk
            .expectBody().returnResult().responseBody?.toUtf8String()

        token.shouldNotBeNull()
        log.debug { "token=$token" }

        // 발급받은 토큰으로 서버에 접근해야 한다
        client.get()
            .uri("/")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith {
                it.responseBody!!.toUtf8String() shouldBeEqualTo "Hello, user!"
            }
    }

    @Test
    @WithMockUser
    fun `MockUser로 인증된 사용자는 서버 접근이 되어야 합니다`() {
        // 인증 정보로 토큰을 발급 받는다
        val token = client.post()
            .uri("/token")
            .exchange()
            .expectStatus().isOk
            .expectBody().returnResult().responseBody?.toUtf8String()

        token.shouldNotBeNull()
        log.debug { "token=$token" }

        // 발급받은 토큰으로 서버에 접근해야 한다
        client.get()
            .uri("/")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith {
                it.responseBody!!.toUtf8String() shouldBeEqualTo "Hello, user!"
            }
    }

    @Test
    fun `인증 안된 user가 새로운 토큰 발급을 요청하면 인증 예외가 발생합니다`() {
        client.post()
            .uri("/token")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `인증 안된 사용자는 서버 접근이 안되어야 합니다`() {
        client.post()
            .uri("/")
            .exchange()
            .expectStatus().isUnauthorized
    }


}
