package io.bluetape4k.workshop.r2dbc.entitycallback

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CustomerRepository: CoroutineCrudRepository<Customer, Long>
