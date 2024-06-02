package io.bluetape4k.workshop.security.server.infrastructure

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.workshop.security.server.ApplicationTest
import io.bluetape4k.workshop.security.server.application.domain.CustomerRepository
import kotlinx.coroutines.flow.toList
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CustomerRepositoryTest(
    @Autowired private val repository: CustomerRepository,
): ApplicationTest() {


    @Test
    fun `insert new customer`() = runSuspendWithIO {
        val customer = randomCustomer()
        repository.insert(customer)

        val loaded = repository.findByEmail(customer.email)
        loaded.shouldNotBeNull() shouldBeEqualTo customer
    }

    @Test
    fun `find all customers`() = runSuspendWithIO {
        val customer1 = randomCustomer()
        val customer2 = randomCustomer()

        repository.insert(customer1)
        repository.insert(customer2)

        repository.all().toList() shouldContainAll listOf(customer1, customer2)
    }

}
