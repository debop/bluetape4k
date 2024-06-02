package io.bluetape4k.workshop.r2dbc.controller

import io.bluetape4k.workshop.r2dbc.domain.User
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import io.bluetape4k.workshop.r2dbc.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.coroutines.CoroutineContext

@RestController
@RequestMapping(path = ["/api"])
class UserController(
    private val service: UserService,
): CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.IO + job

    @GetMapping("/users")
    fun findAll(): Flow<User> {
        return service.findAll()
    }

    @GetMapping("/users/search")
    fun search(@RequestParam(name = "email", required = false) email: String?): Flow<User> {
        if (email.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not provide email to search")
        }
        return service.findByEmail(email)
    }

    @GetMapping("/users/{id}")
    suspend fun findUserById(@PathVariable("id") id: Int): User? {
        return service.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User[$id] not found")
    }

    @PostMapping("/users")
    suspend fun addUser(@RequestBody newUser: UserDTO): User? {
        return service.addUser(newUser)
    }

    @PutMapping("/users/{id}")
    suspend fun updateUser(@PathVariable("id") id: Int, @RequestBody userToUpdate: UserDTO): User? {
        return service.updateUser(id, userToUpdate)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User[$id] not found")
    }

    @DeleteMapping("/users/{id}")
    suspend fun deleteUser(@PathVariable("id") id: Int): Boolean {
        val deleted = service.deleteUser(id)
        if (!deleted) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User[$id] not found")
        }
        return deleted
    }
}
