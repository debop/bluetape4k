package io.bluetape4k.workshop.security.server.infrastructure

import io.bluetape4k.coroutines.flow.extensions.log
import io.bluetape4k.workshop.security.server.application.domain.Customer
import io.bluetape4k.workshop.security.server.application.domain.CustomerRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Repository

@Repository
class CustomerMongoRepository(
    private val customerSpringMongoRepository: CustomerSpringMongoRepository,
): CustomerRepository, CustomerSpringMongoRepository by customerSpringMongoRepository {

    override suspend fun insert(customer: Customer): Customer {
        return customerSpringMongoRepository.save(customer)
    }

    override fun all(): Flow<Customer> {
        return findAll().log("all")
    }

    override suspend fun findByEmail(email: String): Customer? {
        return findCustomerByEmail(email)
    }

}
