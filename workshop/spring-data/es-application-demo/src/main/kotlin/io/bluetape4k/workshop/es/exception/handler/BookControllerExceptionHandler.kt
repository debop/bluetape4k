package io.bluetape4k.workshop.es.exception.handler

import io.bluetape4k.workshop.es.domain.exception.BookNotFoundException
import io.bluetape4k.workshop.es.domain.exception.DuplicatedIsbnException
import io.bluetape4k.workshop.es.exception.EsDemoException
import kotlinx.coroutines.reactor.mono
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@RestControllerAdvice
class BookControllerExceptionHandler {

    @ExceptionHandler(value = [BookNotFoundException::class, DuplicatedIsbnException::class])
    fun doHandleBookException(ex: EsDemoException): Mono<ResponseEntity<Body>> = mono {
        ResponseEntity.badRequest().body(Body(ex.message!!))
    }

    data class Body(
        val message: String,
    )
}
