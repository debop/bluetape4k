package io.bluetape4k.aws.dynamodb.examples.food.tests

import io.bluetape4k.aws.dynamodb.examples.food.AbstractFoodApplicationTest
import io.bluetape4k.aws.dynamodb.examples.food.model.FoodDocument
import io.bluetape4k.aws.dynamodb.examples.food.model.FoodState
import io.bluetape4k.aws.dynamodb.examples.food.repository.FoodRepository
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContainAny
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.random.Random

class FoodRepositoryTest: AbstractFoodApplicationTest() {

    companion object: KLogging()

    @Autowired
    private lateinit var repository: FoodRepository

    @Test
    fun `save one food and load`() = runSuspendWithIO {
        val food = FoodDocument(
            TimebasedUuid.nextBase62String(),
            "42",
            FoodState.COOKING,
            Instant.now().minusSeconds(60_000L)
        )
        log.info { "Save food. $food" }
        repository.save(food)

        yield()

        val foods = repository.findByPartitionKey(
            food.partitionKey,
            Instant.now().minusSeconds(90_000L),
            Instant.now()
        ).toList()

        foods shouldContain food
    }


    @Test
    fun `batch write`() = runSuspendWithIO {
        // DynamoDB Batch Write의 최대 크기가 25 임
        val foods = createFoods(100)
        val result = repository.saveAll(foods)
        result.all { it.unprocessedPutItemsForTable(repository.table).isEmpty() }.shouldBeTrue()

        yield()

        val food = foods.first()
        val loadedFoods = repository.findByPartitionKey(
            food.partitionKey,
            Instant.now().minusSeconds(100L),
            Instant.now()
        ).toList()

        loadedFoods.shouldNotBeEmpty()
    }

    @Test
    fun `delete item`() = runSuspendWithIO {
        val food = createFoods(1).first()
        repository.save(food)

        val deleted = repository.delete(food)
        deleted.shouldNotBeNull()
    }

    @Test
    fun `delete all items`() = runSuspendTest(Dispatchers.IO) {
        val foods = createFoods(100)
        val result = repository.saveAll(foods)
        result.all { it.unprocessedPutItemsForTable(repository.table).isEmpty() }.shouldBeTrue()

        repository.deleteAll(foods)

        val food = foods.first()
        val loadedFoods = repository.findByPartitionKey(
            food.partitionKey,
            Instant.now().minusSeconds(1000L),
            Instant.now()
        ).toList()

        loadedFoods shouldNotContainAny foods
    }

    @Test
    fun `delete all items by key`() = runSuspendWithIO {
        val foods = createFoods(100)
        val result = repository.saveAll(foods)
        result.all { it.unprocessedPutItemsForTable(repository.table).isEmpty() }.shouldBeTrue()

        repository.deleteAllByKeys(foods.map { it.key })

        val food = foods.first()
        val loadedFoods = repository.findByPartitionKey(
            food.partitionKey,
            Instant.now().minusSeconds(100L),
            Instant.now()
        ).toList()

        loadedFoods shouldNotContainAny foods
    }

    private fun createFoods(size: Int = 100): List<FoodDocument> {
        return List(size) {
            FoodDocument(
                id = snowflake.nextId().toString(),
                restraurantId = Random.nextInt(5).toString(),
                state = FoodState.COOKING,
                updatedAt = Instant.now()
            )
        }
    }
}
