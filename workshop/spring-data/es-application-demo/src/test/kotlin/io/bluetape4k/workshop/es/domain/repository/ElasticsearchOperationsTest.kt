package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsDemoTest
import io.bluetape4k.workshop.es.domain.model.Book
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations

class ElasticsearchOperationsTest: AbstractEsDemoTest() {

    companion object: KLogging()

    @Autowired
    private val operations: ElasticsearchOperations = uninitialized()

    @Autowired
    private val bookRepository: BookRepository = uninitialized()


    @Test
    fun `search all books`() = runSuspendWithIO {
        val books = List(10) { createBook() }

        val saved = operations.save(books)

        saved.forEach {
            log.debug { "saved=$it" }
        }

        val loaded = operations.search(NativeQuery.builder().build(), Book::class.java)
            .stream().map { it.content }.toList()

        // val loaded = bookRepository.findAll().toList()
        loaded shouldHaveSize books.size
    }
}
