package io.bluetape4k.aws.dynamodb.examples.food.repository

import io.bluetape4k.aws.dynamodb.enhanced.table
import io.bluetape4k.aws.dynamodb.examples.food.model.FoodDocument
import io.bluetape4k.aws.dynamodb.examples.food.model.Schema
import io.bluetape4k.aws.dynamodb.model.dynamoDbKeyOf
import io.bluetape4k.aws.dynamodb.model.queryEnhancedRequest
import io.bluetape4k.aws.dynamodb.repository.DynamoDbCoroutineRepository
import io.bluetape4k.aws.dynamodb.repository.findFirst
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import java.time.Instant

@Repository
class FoodRepository(
    @Autowired override val client: DynamoDbEnhancedAsyncClient,
    @Value("\${aws.dynamodb.tablePrefix:local-}") tablePrefix: String,
): DynamoDbCoroutineRepository<FoodDocument> {

    companion object: KLogging()

    override val itemClass: Class<FoodDocument> = FoodDocument::class.java
    override val table: DynamoDbAsyncTable<FoodDocument> by lazy {
        client.table("$tablePrefix${Schema.TABLE_NAME}")
    }

    fun findByPartitionKey(
        partitionKey: String,
        updatedAtFrom: Instant,
        updatedAtTo: Instant,
    ): Flow<FoodDocument> = flow {
        val fromKey = dynamoDbKeyOf(partitionKey, updatedAtFrom.toString())
        val toKey = dynamoDbKeyOf(partitionKey, updatedAtTo.toString())

        val queryRequest = queryEnhancedRequest {
            queryConditional(QueryConditional.sortBetween(fromKey, toKey))
        }
        log.info { "queryRequest=$queryRequest" }
        val results = table.index(Schema.IDX_PK_UPDATED_AT).query(queryRequest).findFirst()
        emitAll(results)
    }
}
