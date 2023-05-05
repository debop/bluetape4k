package io.bluetape4k.aws.dynamodb.repository

import io.bluetape4k.aws.dynamodb.enhanced.batchWriteItems
import io.bluetape4k.aws.dynamodb.model.DynamoDbEntity
import io.bluetape4k.aws.dynamodb.model.dynamoDbKeyOf
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest

interface DynamoDbCoroutineRepository<T: DynamoDbEntity> {

    companion object: KLogging()

    val client: DynamoDbEnhancedAsyncClient
    val table: DynamoDbAsyncTable<T>
    val itemClass: Class<T>

    suspend fun findByKey(key: Key): T? {
        return table.getItem(key).await()
    }

    fun findFirst(request: QueryEnhancedRequest): Flow<T> {
        return table.query(request).findFirst()
    }

    fun findFirstByPartitionKey(partitionKey: String): Flow<T> {
        val request = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(dynamoDbKeyOf(partitionKey)))
            .build()
        return findFirst(request)
    }

    suspend fun count(request: QueryEnhancedRequest): Long {
        return table.query(request).count()
    }

    suspend fun save(item: T) {
        table.putItem(item).await()
    }

    suspend fun saveAll(items: Collection<T>): List<BatchWriteResult> {
        return client.batchWriteItems(itemClass, table, items = items)
    }

    suspend fun update(item: T): T? {
        return table.updateItem(item).await()
    }

    suspend fun delete(item: T): T? {
        return table.deleteItem(item.key).await()
    }

    suspend fun delete(key: Key): T? {
        return table.deleteItem(key).await()
    }

    suspend fun deleteAll(items: Iterable<T>): List<T> {
        return items.asFlow()
            .flatMapMerge { item -> flowOf(delete(item)) }
            .mapNotNull { it }
            .toList()
    }

    suspend fun deleteAllByKeys(keys: Iterable<Key>): List<T> {
        return keys.asFlow()
            .flatMapMerge { key -> flowOf(delete(key)) }
            .mapNotNull { it }
            .toList()
    }
}
