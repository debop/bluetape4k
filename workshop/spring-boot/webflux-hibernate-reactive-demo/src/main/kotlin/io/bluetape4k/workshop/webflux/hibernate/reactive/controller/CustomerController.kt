package io.bluetape4k.workshop.webflux.hibernate.reactive.controller

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import io.bluetape4k.workshop.webflux.hibernate.reactive.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
class CustomerController(private val repository: CustomerRepository) {

    @GetMapping("/{id}")
    suspend fun getCustomer(@PathVariable id: Long): Customer? {
        return repository.findById(id) ?: throw java.util.NoSuchElementException("Customer[$id] not found.")
    }

    @GetMapping
    fun getAll(): Flow<Customer> = flow {
        emitAll(repository.findAll().asFlow())
    }

    @GetMapping("/search")
    fun findByCity(@RequestParam(name = "city") city: String): Flow<Customer> = flow {
        emitAll(repository.findByCity(city).asFlow())
    }

    @PostMapping
    suspend fun insertCustomer(@RequestBody name: String): Customer {
        name.requireNotBlank("name")
        return repository.createCustomer(name)
    }

    @PutMapping("/{id}")
    suspend fun updateCustomer(@PathVariable(name = "id") id: Long, @RequestBody customer: Customer): Customer? {
        require(customer.id == id) { "id[$id] is difference with customer.id[${customer.id}]" }
        return repository.updateCustomer(customer)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteCustomer(@PathVariable id: Long): Customer? {
        return repository.deleteCustomerById(id)
    }
}
