package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsWebfluxDemoTest
import io.bluetape4k.workshop.es.domain.model.Book
import kotlinx.coroutines.flow.toList
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate

class BookRepositoryTest: AbstractEsWebfluxDemoTest() {

    companion object: KLogging()

    @Autowired
    private val repository: BookRepository = uninitialized()

    @Autowired
    private val template: ReactiveElasticsearchTemplate = uninitialized()

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()
        template.shouldNotBeNull()
    }


    @Test
    fun `search all books`() = runSuspendWithIO {
        val saved = createRandomBooks(10)
        refreshBookIndex()

        saved.forEach {
            log.debug { "saved book=$it" }
        }

        val loaded = repository.findAll().toList()

        loaded shouldHaveSize saved.size
        loaded shouldBeEqualTo saved
    }

    @Test
    fun `find by id`() = runSuspendWithIO {
        val saved = createRandomBooks(3)

        // id 로 찾는 것은 refresh 할 필요 없습니다.
        // refreshBookIndex()

        val found = repository.findById(saved.last().id!!)
        found shouldBeEqualTo saved.last()
    }

    @Test
    fun `find by not existing id`() = runSuspendWithIO {
        createRandomBooks(3)

        // id 로 찾는 것은 refresh 할 필요 없습니다.
        // refreshBookIndex()

        val found = repository.findById("not-exists")
        found.shouldBeNull()
    }

    @Test
    fun `find by isbn`() = runSuspendWithIO {
        val saved = createRandomBooks(3)
        val last = saved.last()

        // 다른 속성으로 즉시 검색하기 위해서는 index refresh가 필요하다.
        refreshBookIndex()

        repository.findByIsbn(last.isbn).shouldBeEqualTo(last)
    }

    @Test
    fun `find by author`() = runSuspendWithIO {
        val saved = createRandomBooks(10)
        val last = saved.last()

        refreshBookIndex()

        val loaded = repository.findByAuthorName(last.authorName).toList()

        loaded.shouldNotBeEmpty()
        loaded shouldContain last
    }

    @Test
    fun `find by title and author`() = runSuspendWithIO {
        val saved = createRandomBooks(10)
        val last = saved.last()

        refreshBookIndex()

        val loaded = repository.findByTitleAndAuthorName(last.title, last.authorName).toList()

        loaded.shouldNotBeEmpty()
        loaded shouldContain last
    }

    @Test
    fun `delete by id`() = runSuspendWithIO {
        val saved = createRandomBooks(3)

        saved.all { repository.existsById(it.id!!) }.shouldBeTrue()

        repository.deleteById(saved.last().id!!)
        repository.existsById(saved.last().id!!).shouldBeFalse()
    }

    protected suspend fun createRandomBooks(size: Int = 3): List<Book> {
        return repository.saveAll(List(size) { createBook() }).toList()
    }

}
