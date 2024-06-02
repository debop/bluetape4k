package io.bluetape4k.workshop.r2dbc.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.r2dbc.domain.User
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import io.bluetape4k.workshop.r2dbc.domain.toModel
import io.bluetape4k.workshop.r2dbc.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(private val repository: UserRepository) {

    companion object: KLogging()

    fun findAll(): Flow<User> = repository.findAll()

    suspend fun findById(id: Int): User? = repository.findById(id)

    fun findByEmail(email: String): Flow<User> = repository.findByEmail(email)

    suspend fun addUser(user: UserDTO): User? {
        log.debug { "Save new user. ${user.toModel()}" }
        return repository.save(user.toModel())
    }

    suspend fun updateUser(id: Int, user: UserDTO): User? {
        return when {
            repository.existsById(id) -> repository.save(user.toModel(withId = id))
            else                      -> null
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return when {
            repository.existsById(id) -> {
                repository.deleteById(id)
                true
            }

            else                      -> false
        }
    }
}
