package io.bluetape4k.workshop.es

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.domain.model.Book
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractEsDemoTest {

    companion object: KLogging() {
        val faker = Fakers.faker
    }

    @Autowired
    protected val operations: ElasticsearchTemplate = uninitialized()

    // NOTE: ES index refresh는 동기방식으로 해야 검색이 제대로 됩니다.
    protected val indexOpsForBook by lazy { operations.indexOps(Book::class.java) }

    @BeforeEach
    fun beforeEach() {
        recreateIndex()
    }

    protected fun createBook(): Book {
        val book = Book(
            title = faker.book().title(),
            authorName = faker.book().author(),
            publicationYear = faker.number().numberBetween(1900, 2020),
            isbn = faker.code().isbn13(),
        )
        log.debug { "create book=$book" }
        return book
    }

    private fun recreateIndex() {
        log.debug { "recreate index for Book" }
        val ops = operations.indexOps(Book::class.java)
        if (ops.exists()) {
            ops.delete()
            ops.create()
        }
    }
}
