package io.bluetape4k.workshop.webflux.hibernate.reactive.controller

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.webflux.hibernate.reactive.AbstractHibernateReactiveTest
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.CustomerDto
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

class CustomerControllerTest(
    @Autowired private val client: WebTestClient,
): AbstractHibernateReactiveTest() {

    companion object: KLogging()

    /**
     * NOTE: 이건 잘못된 사용 예입니다. Entity 를 DTO 처럼 보낼 수는 없습니다. ㅎㅎ
     */
    @Test
    fun `get all customers`() = runSuspendWithIO {
        val customers = client.get()
            .uri("/customers")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList<CustomerDto>()
            .returnResult().responseBody!!

        customers.shouldNotBeEmpty()
        customers.size shouldBeGreaterThan 2

        customers.forEach { customer ->
            log.debug { "Customer[${customer.name}] live in ${customer.cityName}" }
        }
    }
}
