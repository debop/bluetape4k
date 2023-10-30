package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import kotlinx.coroutines.flow.toList
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ElasticsearchOperationsTest: AbstractEsDemoTest() {

    companion object: KLogging()

    @Autowired
    private val bookRepository: BookRepository = uninitialized()

    @Test
    fun `search all books`() = runSuspendWithIO {
        val books = List(10) { createBook() }

        val saved = operations.save(books)
        indexOpsForBook.refresh()

        saved.forEach {
            log.debug { "saved=$it" }
        }

        val loaded = bookRepository.findAll().toList()
        loaded shouldHaveSize books.size
    }
}
