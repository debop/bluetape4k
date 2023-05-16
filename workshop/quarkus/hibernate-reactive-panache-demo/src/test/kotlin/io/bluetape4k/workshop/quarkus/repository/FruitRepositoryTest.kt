package io.bluetape4k.workshop.quarkus.repository

import io.bluetape4k.quarkus.kotlin.panache.withPanacheRollback
import io.bluetape4k.quarkus.kotlin.panache.withPanacheRollbackSuspending
import io.bluetape4k.quarkus.kotlin.panache.withPanacheTransactionSuspending
import io.bluetape4k.workshop.quarkus.model.Fruit
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.core.Vertx
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Test
import java.time.Duration
import javax.inject.Inject
import kotlin.random.Random

@QuarkusTest
class FruitRepositoryTest {

    @Inject
    internal lateinit var repository: FruitRepository

    @Inject
    internal lateinit var vertx: Vertx

    val grape = newGrape()

    private fun newGrape(): Fruit = Fruit("Graph", "Summer fruit")
    private fun newRandomFruit(): Fruit = Fruit(
        name = "Fruit-${Random.nextLong(100, 99999)}",
        description = "Random Fruit - ${Random.nextLong()}"
    )

    @Test
    fun `find by name with Uni`() {
        val fruit = withPanacheRollback { _, _ ->
            repository
                .persist(newGrape())
                .replaceWith(repository.findByName(grape.name))
        }.await().atMost(Duration.ofSeconds(10))

        fruit.id.shouldNotBeNull()
        fruit.name shouldBeEqualTo grape.name
    }

    @Test
    fun `find by name with Mutiny`() = runTest {
        val fruit = withPanacheRollback { _, _ ->
            repository
                .persist(newGrape())
                .replaceWith(repository.findByName(grape.name))
        }.awaitSuspending()

        fruit.id.shouldNotBeNull()
        fruit.name shouldBeEqualTo grape.name
    }

    @Test
    fun `find by name with vertx and coroutines`() = runTest {
        /*
        2022-03-28 15:03:07,851 DEBUG [org.hib.SQL] (vert.x-eventloop-thread-1 @coroutine#2) insert into Fruit (description, name) values (?, ?)
        2022-03-28 15:03:07,866 DEBUG [org.hib.SQL] (vert.x-eventloop-thread-1) select fruit0_.id as id1_0_, fruit0_.description as descript2_0_, fruit0_.name as name3_0_ from Fruit fruit0_ where fruit0_.name=? limit ?
         */
        withPanacheTransactionSuspending { _, tx: Mutiny.Transaction ->
            // HINT: withRollbackAndAwait 을 사용해도 됩니다.
            tx.markForRollback()
            repository.persist(newGrape()).awaitSuspending()

            val fruit = repository.findByName(grape.name).awaitSuspending()

            fruit.id.shouldNotBeNull()
            fruit.name shouldBeEqualTo grape.name
        }
    }

    @Test
    fun `with transaction await`() = runTest {
        withPanacheRollbackSuspending { _, _ ->
            repository.persist(newGrape()).awaitSuspending()
            // 추가적인 많은 작업을 실행
            repeat(10) {
                repository.persist(newRandomFruit()).awaitSuspending()
            }
            val fruit = repository.findByName(grape.name).awaitSuspending()
            fruit.id.shouldNotBeNull()
            fruit.name shouldBeEqualTo grape.name
        }
    }

    @Test
    fun `mutiny session with transaction await`() = runTest {
        withPanacheRollbackSuspending { _, _ ->
            repository.persist(newGrape()).awaitSuspending()
            val fruit = repository.findByName(grape.name).awaitSuspending()
            fruit.id.shouldNotBeNull()
            fruit.name shouldBeEqualTo grape.name
        }
    }
}
