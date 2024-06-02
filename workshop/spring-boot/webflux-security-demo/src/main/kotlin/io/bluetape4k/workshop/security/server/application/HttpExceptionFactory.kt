package io.bluetape4k.workshop.security.server.application

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object HttpExceptionFactory {

    fun badRequest() = ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request")

    fun unauthorized() = ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
}
