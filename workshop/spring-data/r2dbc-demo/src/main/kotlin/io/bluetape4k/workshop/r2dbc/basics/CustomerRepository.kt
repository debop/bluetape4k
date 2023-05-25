package io.bluetape4k.workshop.r2dbc.basics

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CustomerRepository: CoroutineCrudRepository<Customer, Long> {

    @Query("select id, firstname, lastname from customer c where c.lastname = :lastname")
    fun findByLastname(lastname: String): Flow<Customer>
}
