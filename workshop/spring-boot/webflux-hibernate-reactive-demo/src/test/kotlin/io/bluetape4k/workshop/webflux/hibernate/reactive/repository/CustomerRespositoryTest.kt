package io.bluetape4k.workshop.webflux.hibernate.reactive.repository

import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.webflux.hibernate.reactive.AbstractHibernateReactiveTest
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CustomerRespositoryTest(
    @Autowired private val customerRepo: CustomerRepository,
    @Autowired private val cityRepo: CityRepository,
): AbstractHibernateReactiveTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        customerRepo.shouldNotBeNull()
    }

    @Test
    fun `find customer by id`() = runSuspendTest {
        val debop = customerRepo.findById(1L)
        log.debug { "Customer[1]=$debop" }
        debop.shouldNotBeNull()

        val kim = customerRepo.findById(2L)
        log.debug { "Customer[2]=$kim" }
        kim.shouldNotBeNull()
    }

    @Test
    fun `find all customers`() = runSuspendTest {
        val customers = customerRepo.findAll()
        customers.forEach {
            log.debug { "Customer=$it, city=${it.city}" }
        }
        customers.shouldNotBeEmpty()
    }

    @Test
    fun `find customers by city`() = runSuspendTest {
        val customers = customerRepo.findByCity("se")
        customers.shouldNotBeEmpty()
    }

    @Test
    fun `create new customer`() = runSuspendTest {
        val hell = City("Hell")
        val customer = Customer("Lucifer").apply { city = hell }
        customerRepo.persist(customer)

        customer.id shouldNotBeEqualTo 0L
        customer.city shouldBeEqualTo hell
    }

    @Test
    fun `delete customer`() = runSuspendTest {
        val custoemers = customerRepo.findAll()
        val last = custoemers.last()
        val deleted = customerRepo.deleteCustomerById(last.identifier)
        deleted shouldBeEqualTo last
    }
}
