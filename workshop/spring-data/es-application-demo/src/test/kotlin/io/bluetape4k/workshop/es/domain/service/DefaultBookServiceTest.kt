package io.bluetape4k.workshop.es.domain.service

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import io.bluetape4k.workshop.es.domain.exception.BookNotFoundException
import io.bluetape4k.workshop.es.domain.exception.DuplicatedIsbnException
import io.bluetape4k.workshop.es.domain.model.Book
import io.bluetape4k.workshop.es.domain.repository.BookRepository
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import kotlin.test.assertFailsWith

class DefaultBookServiceTest: AbstractEsDemoTest() {

    companion object: KLogging()

    @Autowired
    private val bookService: BookService = uninitialized()

    @Autowired
    private val bookRepository: BookRepository = uninitialized()

    @Autowired
    private val reactiveOperations: ReactiveElasticsearchTemplate = uninitialized()


    @Test
    fun `context loading`() {
        bookService.shouldNotBeNull()
        bookRepository.shouldNotBeNull()
        reactiveOperations.shouldNotBeNull()
    }

    @Test
    fun `get all books`() = runTest {

        val books = List(2) { bookService.create(createBook()) }
        indexOpsForBook.refresh()
        books.forEach {
            log.debug { "saved book=$it" }
        }

        val loaded = bookService.getAll()
        loaded shouldHaveSize 2
        loaded shouldBeEqualTo books
    }

    @Test
    fun `get book by isbn`() = runSuspendWithIO {
        val book = createBook()

        val saved = bookService.create(book)
        indexOpsForBook.refresh()
        log.debug { "saved=$saved" }

        val loaded = bookService.getByIsbn(book.isbn)!!
        loaded shouldBeEqualTo saved
    }

    @Test
    fun `find by title and author`() = runSuspendWithIO {
        val saved = List(4) { bookService.create(createBook()) }
        indexOpsForBook.refresh()

        val loaded = bookService.findByTitleAndAuthor(saved[0].title, saved[0].authorName!!)

        loaded.size shouldBeGreaterOrEqualTo 1
    }

    @Test
    fun `find by title and author with custom book`() = runSuspendWithIO {
        bookService.create(Book("12 rules for life", "Jordan Peterson", 2018, "978-0345816023"))
        bookService.create(Book("Rules or not rules?", "Jordan Miller", 2010, "978128000000"))
        bookService.create(Book("Poor economy", "Jordan Miller", 2006, "9781280789000"))
        bookService.create(Book("The Cathedral and the Bazaar", "Eric Raymond", 1999, "9780596106386"))

        indexOpsForBook.refresh()

        val found = bookService.findByTitleAndAuthor("rules", "jordan")

        found.forEach {
            log.debug { "found book=$it" }
        }
        found shouldHaveSize 2
    }

    @Test
    fun `create book`() = runTest {
        val createdBook = bookService.create(createBook())
        createdBook.id.shouldNotBeNull()
    }

    @Test
    fun `create book with duplicate isbn throw exception`() = runTest {
        val createdBook = bookService.create(createBook())
        createdBook.id.shouldNotBeNull()
        indexOpsForBook.refresh()

        assertFailsWith<DuplicatedIsbnException> {
            val duplicatedBook = createdBook.copy(title = "Test title", id = null)
            bookService.create(duplicatedBook)
        }
    }

    @Test
    fun `delete book by id`() = runTest {
        val createdBook = bookService.create(createBook())
        createdBook.id.shouldNotBeNull()
        indexOpsForBook.refresh()

        bookService.deleteById(createdBook.id!!)
        indexOpsForBook.refresh()

        val loaded = bookRepository.findById(createdBook.id!!)
        loaded.shouldBeNull()
    }

    @Test
    fun `update book`() = runTest {
        val createdBook = bookService.create(createBook())
        createdBook.id.shouldNotBeNull()
        indexOpsForBook.refresh()

        val updatedBook = createdBook.copy(title = "Updated title", id = null)
        val updated = bookService.update(createdBook.id!!, updatedBook)
        indexOpsForBook.refresh()

        updated.title shouldBeEqualTo updatedBook.title
        updated shouldBeEqualTo createdBook.copy(title = updatedBook.title)
    }

    @Test
    fun `update book throws exception if cannot found book`() = runTest {
        val bookToUpdate = createBook()

        assertFailsWith<BookNotFoundException> {
            bookService.update("not-found-id", bookToUpdate)
        }
    }
}
