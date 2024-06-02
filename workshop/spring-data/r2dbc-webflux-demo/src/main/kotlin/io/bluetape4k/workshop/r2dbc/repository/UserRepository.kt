package io.bluetape4k.workshop.r2dbc.repository

import io.bluetape4k.workshop.r2dbc.domain.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository: CoroutineCrudRepository<User, Int> {

    @Query("SELECT u.* FROM users u WHERE u.email = :email")
    fun findByEmail(email: String): Flow<User>

}
