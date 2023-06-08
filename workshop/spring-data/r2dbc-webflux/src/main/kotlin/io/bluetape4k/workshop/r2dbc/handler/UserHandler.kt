package io.bluetape4k.workshop.r2dbc.handler

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.support.asIntOrNull
import io.bluetape4k.workshop.r2dbc.domain.ErrorMessage
import io.bluetape4k.workshop.r2dbc.domain.UserDTO
import io.bluetape4k.workshop.r2dbc.service.UserService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.json

@Component
class UserHandler(private val service: UserService) {

    companion object: KLogging()

    suspend fun findAll(request: ServerRequest): ServerResponse {
        val users = service.findAll()
        return ServerResponse.ok().json().bodyAndAwait(users)
    }

    suspend fun search(request: ServerRequest): ServerResponse {
        val criteria = request.queryParams()

        return when {
            criteria.isEmpty()            ->
                errorResponse(HttpStatus.BAD_REQUEST, "Search must have query parameter")

            criteria.containsKey("email") -> {
                val criteriaValue = criteria.getFirst("email")
                if (criteriaValue.isNullOrBlank()) {
                    errorResponse(HttpStatus.BAD_REQUEST, "Not provide email to search")
                } else {
                    ServerResponse.ok().json().bodyAndAwait(service.findByEmail(criteriaValue))
                }
            }

            else                          ->
                errorResponse(HttpStatus.BAD_REQUEST, "Not supported search criteria")
        }
    }

    suspend fun findUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").asIntOrNull()
                 ?: return errorResponse(HttpStatus.BAD_REQUEST, "`id` must be numeric")

        return service.findById(id)?.let { user ->
            ServerResponse.ok().json().bodyValueAndAwait(user)
        } ?: errorResponse(HttpStatus.NOT_FOUND, "User[$id] not found.")
    }

    suspend fun addUser(request: ServerRequest): ServerResponse {
        val newUser: UserDTO = runCatching { request.bodyToMono<UserDTO>().awaitSingleOrNull() }
                                   .onFailure { log.error(it) { "Fail to decode body" } }
                                   .getOrNull()
                               ?: return errorResponse(HttpStatus.BAD_REQUEST, "Invalid body")

        return service.addUser(newUser)
                   ?.let { user ->
                       ServerResponse.status(HttpStatus.CREATED).json().bodyValueAndAwait(user)
                   }
               ?: errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error")
    }

    suspend fun updateUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").asIntOrNull()
                 ?: return errorResponse(HttpStatus.BAD_REQUEST, "`id` must be numeric")

        val userToUpdate: UserDTO = runCatching { request.bodyToMono<UserDTO>().awaitSingleOrNull() }
                                        .onFailure { log.error(it) { "Fail to decode body" } }
                                        .getOrNull()
                                    ?: return errorResponse(HttpStatus.BAD_REQUEST, "Invalid body")

        return service.updateUser(id, userToUpdate)?.let { user ->
            ServerResponse.ok().json().bodyValueAndAwait(user)
        } ?: errorResponse(HttpStatus.NOT_FOUND, "User[$id] not found")
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").asIntOrNull()
                 ?: return errorResponse(HttpStatus.BAD_REQUEST, "`id` must be numeric")

        return if (service.deleteUser(id)) ServerResponse.noContent().buildAndAwait()
        else errorResponse(HttpStatus.NOT_FOUND, "User[$id] not found")
    }

    private suspend fun errorResponse(status: HttpStatus = HttpStatus.BAD_REQUEST, message: String): ServerResponse {
        return ServerResponse.status(status).json().bodyValueAndAwait(ErrorMessage(message))
    }
}
