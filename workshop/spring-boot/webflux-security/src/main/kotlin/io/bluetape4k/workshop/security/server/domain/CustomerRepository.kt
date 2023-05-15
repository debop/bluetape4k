package io.bluetape4k.workshop.security.server.domain

import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun insert(customer: Customer): Customer

    fun all(): Flow<Customer>

    suspend fun findByEmail(email: String): Customer?
}
