package io.bluetape4k.data.hibernate.reactive.examples.mutiny

import io.bluetape4k.data.hibernate.reactive.examples.model.Author
import io.bluetape4k.data.hibernate.reactive.examples.model.Author_
import io.bluetape4k.data.hibernate.reactive.examples.model.Book
import io.bluetape4k.data.hibernate.reactive.examples.model.Book_
import io.bluetape4k.data.hibernate.reactive.mutiny.findAs
import io.bluetape4k.data.hibernate.reactive.mutiny.withSessionAndAwait
import io.bluetape4k.data.hibernate.reactive.mutiny.withTransactionAndAwait
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.time.LocalDate
import javax.persistence.criteria.CriteriaQuery
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.hibernate.graph.RootGraph
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MutinySessionFactoryExamples: AbstractMutinyTest() {

    private val author1 = Author("Debop")
    private val author2 = Author("Sunghyouk Bae")
    private val book1 = Book("1-85723-235-6", "Feersum Endjinn", LocalDate.of(1994, 1, 1))
    private val book2 = Book("0-380-97346-4", "Cryptonomicon", LocalDate.of(1999, 5, 1))
    private val book3 = Book("0-553-08853-X", "Snow Crash", LocalDate.of(1992, 6, 1))


    @BeforeAll
    fun beforeAll() = runSuspendTest {
        author1.addBook(book1)
        author2.addBook(book2)
        author2.addBook(book3)

        sf.withTransactionAndAwait { session ->
            session.persistAll(author1, author2).awaitSuspending()
        }
    }

    @Test
    fun `mutiny session example`() = runSuspendTest {
        sf.withSessionAndAwait { session -> // NOTE: many-to-one 을 lazy로 fetch 하기 위해서 EntityGraph나 @FetchProfile 을 사용해야 합니다.
            val book = session.enableFetchProfile("withAuthor").findAs<Book>(book2.id).awaitSuspending()

            book.shouldNotBeNull()
            book.author.shouldNotBeNull()
        }

        val authors = sf.withSessionAndAwait { session ->
            session.findAs<Author>(author1.id, author2.id).awaitSuspending()
        }
        authors shouldBeEqualTo listOf(author1, author2)
    }

    @Test
    fun `find all book with fetch join`() = runSuspendTest {
        val sql = "SELECT b FROM Book b LEFT JOIN FETCH b.author a"
        val books = sf.withSessionAndAwait { session ->
            session.createQuery<Book>(sql).resultList.awaitSuspending()
        }
        books.forEach {
            println(it)
            println("\t${it.author}")
        }
        books shouldHaveSize 3
    }


    @Test
    fun `find all by entity graph`() = runSuspendTest {
        val criteria = sf.criteriaBuilder.createQuery(Book::class.java)
        val root = criteria.from(Book::class.java)
        criteria.select(root)

        val books = sf.withSessionAndAwait { session ->
            val graph = session.createEntityGraph(Book::class.java)
            graph.addAttributeNodes(Book::author.name)

            val query: Mutiny.Query<Book> = session.createQuery(criteria)
            query.setPlan(graph)

            query.resultList.awaitSuspending()
        }
        books.forEach {
            println(it)
        }
        books shouldHaveSize 3
    }

    @Test
    fun `find book by author name`() = runSuspendTest {
        val cb = sf.criteriaBuilder
        val criteria = cb.createQuery(Book::class.java)
        val book = criteria.from(Book::class.java)
        val author = book.join(Book_.author)

        criteria.select(book).where(cb.equal(author.get(Author_.name), "Debop"))

        val books = sf.withSessionAndAwait { session ->
            val graph = session.createEntityGraph(Book::class.java)
            graph.addAttributeNodes(Book_.author)

            val query = session.createQuery(criteria)
            query.setPlan(graph)
            query.resultList.awaitSuspending()
        }
        books.forEach {
            println(it)
        }
        books shouldHaveSize 1
    }

    @Test
    fun `find all authors by book isbn`() = runSuspendTest {
        val cb = sf.criteriaBuilder
        val criteria = cb.createQuery(Author::class.java)
        val author = criteria.from(Author::class.java)
        val book = author.join(Author_.books)
        criteria.select(author).where(cb.equal(book.get(Book_.isbn), book1.isbn))

        val authors = sf.withSessionAndAwait { session ->
            session.createQuery(criteria).resultList.awaitSuspending()
        } // NOTE: author 만 로딩했으므로, books 에 접근하면 lazy initialization 예외가 발생합니다.
        authors.forEach {
            println(it) //            it.books.forEach { book ->
            //                println("book=$book")
            //            }
        }
    }


    @Test
    fun `find author and book by book isbn`() = runSuspendTest {
        val cb = sf.criteriaBuilder
        val criteria: CriteriaQuery<Author> = cb.createQuery(Author::class.java)
        val author = criteria.from(Author::class.java)
        val book = author.join(Author_.books)

        // where 조건
        criteria.select(author).where(cb.equal(book.get(Book_.isbn), book2.isbn))

        val authors = sf.withSessionAndAwait { session -> // inner join fetch
            val graph = session.createEntityGraph(Author::class.java).apply {
                addAttributeNodes(Author_.books)
            } as RootGraph<Author>

            session.createQuery(criteria).setPlan(graph).resultList.awaitSuspending()
        }

        authors.forEach { a ->
            println(a)
            a.books.forEach { b ->
                println("\t$b")
            }
        }
        authors shouldHaveSize 1
        authors.forEach {
            it.books shouldHaveSize 1
        }
    }

    @Test
    fun `find author by book isbn and fetch book`() = runSuspendTest {
        val cb = sf.criteriaBuilder
        val criteria: CriteriaQuery<Author> = cb.createQuery(Author::class.java)
        val author = criteria.from(Author::class.java)
        val book = author.join(Author_.books)

        // where 조건
        criteria.select(author).where(cb.equal(book.get(Book_.isbn), book2.isbn))

        val authors = sf.withSessionAndAwait { session ->
            session.createQuery(criteria).resultList.awaitSuspending().apply {
                forEach { author ->
                    session.fetch(author.books).awaitSuspending()
                }
            }
        }
        authors.forEach { a ->
            println(a)
            a.books.forEach { b ->
                println("\t$b")
            }
        }
        authors shouldHaveSize 1
        authors.forEach {
            it.books shouldHaveSize 2
        }
    }
}
