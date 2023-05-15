package io.bluetape4k.workshop.security.server.application.controller

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.security.server.ApplicationTest
import io.bluetape4k.workshop.security.server.domain.Customer
import io.bluetape4k.workshop.security.server.domain.CustomerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.expectBodyList

internal class CustomerControllerTest(
    @Autowired private val repository: CustomerRepository
): ApplicationTest() {

    companion object: KLogging()

    @Test
    fun `Authorization 헤더 값 없이 API 를 호출하면 UNAUTHORIZED 에러를 받는다`() {
        client
            .get().uri("/v1/customers")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `Authorization 헤더에 Bearer 가 누락되어도 UNAUTHORIZED 예러가 발생한다`() {
        client
            .get().uri("/v1/customers")
            .header(HttpHeaders.AUTHORIZATION, accessToken().replace("Bearer ", ""))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `Authorization 헤더에 잘못된 토큰이라면 UNAUTHORIZED 예러가 발생한다`() {
        client
            .get().uri("/v1/customers")
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `등록된 사용자의 인증정보를 Authorization 토큰으로 제공하면 API 호출이 성공한다`() = runSuspendWithIO {
        val customer = randomCustomer()
        repository.insert(customer)

        client.get()
            .uri("/v1/customers")
            .header(HttpHeaders.AUTHORIZATION, accessToken())
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList<Customer>().contains(customer)
    }
}
