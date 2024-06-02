package io.bluetape4k.workshop.security.server.application.controller

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.workshop.security.server.ApplicationTest
import io.bluetape4k.workshop.security.server.application.domain.Customer
import io.bluetape4k.workshop.security.server.application.domain.CustomerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.expectBodyList

class AdminCustomerControllerTest(
    @Autowired private val customerRepository: CustomerRepository,
): ApplicationTest() {

    @Test
    fun `일반 사용자 토큰을 제공하면 Unauthorized 예외가 발생한다`() {
        client.get()
            .uri("/admin/customers")
            .header(HttpHeaders.AUTHORIZATION, accessToken())
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `ADMIN Role의 토큰을 제공하면 Unauthorized 예외가 발생한다`() = runSuspendWithIO {
        val customer = randomCustomer()
        customerRepository.insert(customer)

        client.get()
            .uri("/admin/customers")
            .header(HttpHeaders.AUTHORIZATION, adminAccessToken())
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList<Customer>().contains(customer)
    }
}
