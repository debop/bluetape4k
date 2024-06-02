package io.bluetape4k.workshop.es.domain.repository

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.debug
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.es.AbstractEsWebfluxDemoTest
import io.bluetape4k.workshop.es.domain.model.Book
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query

class ReactiveElasticsearchOperationsTest: AbstractEsWebfluxDemoTest() {

    @Autowired
    private val reactiveOps: ReactiveElasticsearchOperations = uninitialized()

    @Test
    fun `context loading`() {
        reactiveOps.shouldNotBeNull()
    }

    @Test
    fun `find all books`() = runSuspendWithIO {
        val saved = createRandomBooks(10)

        val query = Query.findAll()
        val loaded = reactiveOps.search(query, Book::class.java).map { it.content }.asFlow().toList()

        loaded.forEach {
            log.debug { "loaded book=$it" }
        }
        loaded shouldBeEqualTo saved
    }

    @Test
    fun `find by isbn`() = runSuspendWithIO {
        val saved = createRandomBooks(3)
        val target = saved.random()

//        val query = QueryBuilders.bool { bqb ->
//            bqb.must { qb ->
//                qb.match { mqb ->
//                    mqb.field("isbn").query(target.isbn)
//                }
//            }
//        }
//        val nativeQuery = NativeQuery.builder().withQuery(query).build()

        // CriteriaQuery 사용 예
        // 참고 : https://juntcom.tistory.com/149
        val criteria = Criteria.where(Book::isbn.name).`is`(target.isbn)
        val criteriaQuery = CriteriaQuery.builder(criteria).build()

        val loaded = reactiveOps.search(criteriaQuery, Book::class.java).map { it.content }.asFlow().toList()

        loaded shouldContain target
    }

    @Test
    fun `find by title and author`() = runSuspendWithIO {
        val saved = createRandomBooks(3)
        val target = saved.random()

        val criteria = Criteria.where(Book::title.name).`is`(target.title)
            .and(Book::authorName.name).`is`(target.authorName)
        val query = CriteriaQuery.builder(criteria).build()

        val loaded = reactiveOps.search(query, Book::class.java).map { it.content }.asFlow().toList()
        loaded shouldContain target
    }

    private suspend fun createRandomBooks(size: Int = 3): List<Book> {
        val books = flow { repeat(size) { emit(createBook()) } }
        return books
            .flatMapMerge {
                flowOf(reactiveOps.save(it).awaitSingle())
            }
            .toList()
            .apply {
                refreshBookIndex()
            }
    }
}
