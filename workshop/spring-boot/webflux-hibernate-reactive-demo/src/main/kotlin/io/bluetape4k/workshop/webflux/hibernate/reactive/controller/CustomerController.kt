package io.bluetape4k.workshop.webflux.hibernate.reactive.controller

import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.CustomerDto
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.toDto
import io.bluetape4k.workshop.webflux.hibernate.reactive.repository.CityRepository
import io.bluetape4k.workshop.webflux.hibernate.reactive.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.datafaker.Faker
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/customers")
class CustomerController(
    private val customerRepo: CustomerRepository,
    private val cityRepo: CityRepository,
) {

    private val faker = Faker()

    @GetMapping("/{id}")
    suspend fun getCustomer(@PathVariable id: Long): CustomerDto? {
        return customerRepo.findById(id)?.toDto() ?: throw java.util.NoSuchElementException("Customer[$id] not found.")
    }

    @GetMapping
    fun getAll(): Flow<CustomerDto> = flow {
        emitAll(customerRepo.findAll().asFlow().map { it.toDto() })
    }

    @GetMapping("/search")
    fun findByCity(@RequestParam(name = "city") city: String): Flow<CustomerDto> = flow {
        emitAll(customerRepo.findByCity(city).map { it.toDto() }.asFlow())
    }

    @PostMapping
    suspend fun insertCustomer(@RequestBody name: String): CustomerDto? {
        name.requireNotBlank("name")
        return customerRepo.createCustomer(name).toDto()
    }

    @PutMapping("/{id}")
    suspend fun updateCustomer(@PathVariable(name = "id") id: Long, @RequestBody customer: Customer): CustomerDto? {
        require(customer.id == id) { "id[$id] is difference with customer.id[${customer.id}]" }
        return customerRepo.updateCustomer(customer)?.toDto()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteCustomer(@PathVariable id: Long): CustomerDto? {
        return customerRepo.deleteCustomerById(id)?.toDto()
    }

    @GetMapping("/random")
    suspend fun insertCustomer(): CustomerDto? {
        val customer = Customer(faker.name().fullName())
        return customerRepo.persist(customer).toDto()
    }
}
