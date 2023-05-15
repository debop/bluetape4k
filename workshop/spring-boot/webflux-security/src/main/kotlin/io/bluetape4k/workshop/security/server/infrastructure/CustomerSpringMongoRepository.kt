package io.bluetape4k.workshop.security.server.infrastructure

import io.bluetape4k.workshop.security.server.domain.Customer
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CustomerSpringMongoRepository: CoroutineCrudRepository<Customer, String> {

    suspend fun findCustomerByEmail(email: String): Customer?
}
