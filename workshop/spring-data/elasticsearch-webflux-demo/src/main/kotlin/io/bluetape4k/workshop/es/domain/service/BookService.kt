package io.bluetape4k.workshop.es.domain.service

import io.bluetape4k.workshop.es.domain.model.Book


interface BookService {

    suspend fun getByIsbn(isbn: String): Book?

    suspend fun getAll(): List<Book>

    suspend fun findByAuthor(authorName: String): List<Book>

    suspend fun findByTitleAndAuthor(title: String, author: String): List<Book>

    suspend fun create(book: Book): Book

    suspend fun createAll(books: Collection<Book>): Collection<Book>

    suspend fun deleteById(id: String)

    suspend fun update(id: String, book: Book): Book
}
