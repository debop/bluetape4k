package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.es.AbstractEsWebfluxDemoTest
import io.bluetape4k.workshop.es.domain.model.Book
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test
import org.springframework.data.elasticsearch.core.query.Query

class ElasticsearchOperationsTest: AbstractEsWebfluxDemoTest() {


    @Test
    fun `search all books`() {
        val books = List(10) { createBook() }

        val saved = operations.save(books)
        indexOpsForBook.refresh()

        saved.forEach {
            log.debug { "saved book=$it" }
        }

        val query = Query.findAll()
        val loaded = operations.search(query, Book::class.java).map { it.content }.toList()

        loaded shouldHaveSize books.size
    }
}
