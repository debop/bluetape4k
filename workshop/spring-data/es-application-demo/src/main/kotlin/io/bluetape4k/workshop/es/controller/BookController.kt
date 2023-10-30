package io.bluetape4k.workshop.es.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.es.domain.model.Book
import io.bluetape4k.workshop.es.domain.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/books")
class BookController(
    private val bookService: BookService,
) {

    companion object: KLogging()

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllBooks(): List<Book> {
        return bookService.getAll()
    }

}
