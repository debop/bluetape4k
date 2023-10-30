package io.bluetape4k.workshop.es.domain.service

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import io.bluetape4k.workshop.es.domain.repository.BookRepository
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate

class DefaultBookServiceTest: AbstractEsDemoTest() {

    companion object: KLogging()

    @Autowired
    private val bookService: BookService = uninitialized()

    @Autowired
    private val bookRepository: BookRepository = uninitialized()

    @Autowired
    private val template: ReactiveElasticsearchTemplate = uninitialized()

    @Test
    fun `context loading`() {
        bookService.shouldNotBeNull()
        bookRepository.shouldNotBeNull()
        template.shouldNotBeNull()
    }

    @Test
    fun `get all books`() = runTest {
        val book1 = createBook()
        val book2 = createBook()

        bookService.create(book1)
        bookService.create(book2)

        val books = bookService.getAll()
        books shouldHaveSize 2
    }

    @Test
    fun `get book by isbn`() = runTest {
        val book = createBook()
        bookService.create(book)

        val result = bookService.getByIsbn(book.isbn)!!

        result.title shouldBeEqualTo book.title
        result.authorName shouldBeEqualTo book.authorName
        result.publicationYear shouldBeEqualTo book.publicationYear
        result.isbn shouldBeEqualTo book.isbn
    }

}
