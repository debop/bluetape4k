package io.bluetape4k.workshop.r2dbc.basics

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TransactionalService(private val repository: CustomerRepository) {

    @Transactional
    suspend fun save(customer: Customer): Customer {
        val saved = repository.save(customer)
        if (saved.firstname == "Dave") {
            error("Dave is not allowed")
        }
        return saved
    }
}
