package io.bluetape4k.workshop.r2dbc.handlers

import io.bluetape4k.workshop.r2dbc.exception.PostNotFoundException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice
class RestWebExceptionHandler {

    @ExceptionHandler(PostNotFoundException::class)
    suspend fun handle(ex: PostNotFoundException, exchange: ServerWebExchange) {
        exchange.response.statusCode = HttpStatus.NOT_FOUND
        exchange.response.setComplete().awaitFirstOrNull()
    }
}
