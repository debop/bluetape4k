package io.bluetape4k.workshop.es.controller

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.es.domain.dto.ModifyBookRequest
import io.bluetape4k.workshop.es.domain.dto.toBook
import io.bluetape4k.workshop.es.domain.exception.BookNotFoundException
import io.bluetape4k.workshop.es.domain.model.Book
import io.bluetape4k.workshop.es.domain.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/books")
class BookController(
    private val bookService: BookService,
) {

    companion object: KLogging()

    @GetMapping
    suspend fun getAllBooks(): List<Book> {
        return bookService.getAll()
    }

    @PostMapping
    suspend fun createBook(@RequestBody @Valid modifyBookRequest: ModifyBookRequest): Book {
        return bookService.create(modifyBookRequest.toBook())
    }

    @GetMapping("/{isbn}")
    suspend fun getBookByIsbn(@PathVariable isbn: String): Book {
        return bookService.getByIsbn(isbn).apply { log.debug { "loaded book=$this" } }
            ?: throw BookNotFoundException("The given isbn($isbn) is not found.")
    }

    @GetMapping("/query")
    suspend fun getBooksByAuthorAndTitle(
        @RequestParam("title") title: String,
        @RequestParam("author") author: String,
    ): List<Book> {
        return bookService.findByTitleAndAuthor(title, author)
    }

    @PutMapping("/{id}")
    suspend fun updateBook(
        @PathVariable id: String,
        @RequestBody @Valid updateBookRequest: ModifyBookRequest,
    ): Book {
        return bookService.update(id, updateBookRequest.toBook())
    }

    @DeleteMapping("/{id}")
    suspend fun deleteBook(@PathVariable id: String) {
        bookService.deleteById(id)
    }
}
