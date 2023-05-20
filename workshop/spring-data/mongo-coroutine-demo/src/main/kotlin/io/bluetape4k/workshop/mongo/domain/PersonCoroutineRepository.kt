package io.bluetape4k.workshop.mongo.domain

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PersonCoroutineRepository: CoroutineCrudRepository<Person, String> {

    suspend fun findPersonByFirstname(firstname: String): Person?

    suspend fun findFirstByFirstname(firstname: String): Person?

    fun findAllByFirstname(firstname: String): Flow<Person>

    fun findAllByLastname(lastname: String): Flow<Person>
}
