package io.bluetape4k.workshop.es.controller

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.workshop.es.AbstractEsWebfluxDemoTest
import io.bluetape4k.workshop.es.domain.dto.toBook
import io.bluetape4k.workshop.es.domain.dto.toModifyBookRequest
import io.bluetape4k.workshop.es.domain.model.Book
import io.bluetape4k.workshop.es.domain.service.BookService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

class BookControllerTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val bookService: BookService,
): AbstractEsWebfluxDemoTest() {

    companion object: KLogging() {
        private const val BOOK_PATH = "/v1/books"
    }

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
        bookService.shouldNotBeNull()
    }

    @Test
    fun `get all books`() = runSuspendWithIO {
        val saved = insertRandomBooks(5)

        val loaded = client.get()
            .uri(BOOK_PATH)
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .asFlow().toList()

        loaded.forEach {
            log.trace { "loaded book=$it" }
        }
        loaded shouldHaveSize saved.size
    }

    @Test
    fun `find book by isbn`() = runSuspendWithIO {
        val saved = insertRandomBooks(3)

        val foundBook = client.get()
            .uri("$BOOK_PATH/${saved.last().isbn}")
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .awaitSingle()

        foundBook.shouldNotBeNull()
        foundBook.isbn shouldBeEqualTo saved.last().isbn
    }

    @Test
    fun `find book with not exists isbn`() = runTest {
        insertRandomBooks(3)

        client.get()
            .uri("$BOOK_PATH/not-exists")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `find book by author and title`() = runSuspendWithIO {
        val saved = insertRandomBooks(3)
        val last = saved.last()
        val title = last.title
        val author = last.authorName

        val foundBooks = client.get()
            .uri("$BOOK_PATH?title=$title&author=$author")
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .asFlow().toList()

        foundBooks.forEach {
            log.trace { "found book=$it" }
        }
        foundBooks.size shouldBeGreaterOrEqualTo 1
    }

    @Test
    fun `create book`() = runSuspendWithIO {
        val book = createBook()

        val createdBook = client.post()
            .uri(BOOK_PATH)
            .bodyValue(book.toModifyBookRequest())
            .exchange()
            .expectStatus().isCreated
            .returnResult<Book>().responseBody
            .awaitSingle()

        createdBook.id.shouldNotBeNull()
        createdBook shouldBeEqualTo book.copy(id = createdBook.id)
    }

    @Test
    fun `update existing book`() = runSuspendWithIO {
        val saved = insertRandomBooks(3)
        val last = saved.last()

        val updateRequest = last.toModifyBookRequest().copy(title = "updated title")

        val updatedBook = client.put()
            .uri("$BOOK_PATH/${last.id}")
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .awaitSingle()

        updatedBook shouldBeEqualTo updateRequest.toBook().copy(id = last.id)
    }

    @Test
    fun `update not existing book`() = runSuspendWithIO {
        val saved = insertRandomBooks(3)
        val last = saved.last()

        val updateRequest = last.toModifyBookRequest().copy(title = "updated title")

        client.put()
            .uri("$BOOK_PATH/not-exisis")
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `delete book by id`() = runSuspendWithIO {
        val saved = insertRandomBooks(3)
        val last = saved.last()

        client.delete()
            .uri("$BOOK_PATH/${last.id}")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `delete book by invalid id`() = runSuspendWithIO {
        insertRandomBooks(3)

        client.delete()
            .uri("$BOOK_PATH/not-exists")
            .exchange()
            .expectStatus().isNotFound
    }

    private suspend fun insertRandomBooks(size: Int = 10): List<Book> {
        return bookService.createAll(List(size) { createBook() }).toList()
            .apply {
                // NOTE: indexOps refresh 해줘야 검색이 됩니다.
                refreshBookIndex()
            }
    }
}
