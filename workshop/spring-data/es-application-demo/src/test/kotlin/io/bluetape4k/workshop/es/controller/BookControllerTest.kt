package io.bluetape4k.workshop.es.controller

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import io.bluetape4k.workshop.es.domain.dto.ModifyBookRequest
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
): AbstractEsDemoTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() = runTest {
        client.shouldNotBeNull()
        bookService.shouldNotBeNull()
    }

    @Test
    fun `get all books`() = runSuspendWithIO {
        val saved = insertBooks(5)

        val loaded = client.get()
            .uri("/v1/books")
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>()
            .responseBody
            .asFlow()
            .toList()

        loaded.forEach {
            log.debug { "loaded book=$it" }
        }
        loaded shouldHaveSize saved.size
    }

    @Test
    fun `find book by isbn`() = runSuspendWithIO {
        val saved = insertBooks(3)

        val foundBook = client.get()
            .uri("/v1/books/${saved.last().isbn}")
            .exchange()
            .returnResult<Book>()
            .responseBody
            .awaitSingle()

        foundBook.shouldNotBeNull()
        foundBook.isbn shouldBeEqualTo saved.last().isbn
    }

    @Test
    fun `find book by not exists isbn`() = runSuspendWithIO {
        val saved = insertBooks(3)

        client.get()
            .uri("/v1/books/not-exists")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `find books by author and title`() = runSuspendWithIO {
        val saved = insertBooks(3)
        val last = saved.last()
        val title = last.title
        val author = last.authorName

        val foundBooks = client.get()
            .uri("/v1/books?title=$title&author=$author")
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .asFlow()
            .toList()

        foundBooks.forEach {
            log.debug { "found book=$it" }
        }
        foundBooks.size shouldBeGreaterOrEqualTo 1
    }

    @Test
    fun `create book`() = runTest {
        val createBookRequest = ModifyBookRequest(
            title = faker.book().title(),
            authorName = faker.book().author(),
            publicationYear = faker.number().numberBetween(1945, 2020),
            isbn = faker.code().isbn13(),
        )

        val createdBook = client.post()
            .uri("/v1/books")
            .bodyValue(createBookRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .awaitSingle()

        createdBook shouldBeEqualTo createBookRequest.toBook().copy(id = createdBook.id)
    }

    @Test
    fun `update existed book`() = runTest {
        val saved = insertBooks(3)
        val last = saved.last()

        val updateBookRequest = last.toModifyBookRequest().copy(isbn = faker.code().isbn13())

        val updatedBook = client.put()
            .uri("/v1/books/${last.id}")
            .bodyValue(updateBookRequest)
            .exchange()
            .expectStatus().isOk
            .returnResult<Book>().responseBody
            .awaitSingle()

        updatedBook shouldBeEqualTo updateBookRequest.toBook().copy(id = last.id)
    }


    @Test
    fun `delete book by id`() = runTest {
        val saved = insertBooks(3)
        val last = saved.last()

        client.delete()
            .uri("/v1/books/${last.id}")
            .exchange()
            .expectStatus().isOk
    }

    private suspend fun insertBooks(size: Int = 10): List<Book> {
        return List(size) { bookService.create(createBook()) }
            .apply {
                // NOTE: indexOps refresh 해줘야 검색이 됩니다.
                indexOpsForBook.refresh()
            }
    }
}
