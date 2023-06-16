package io.bluetape4k.aws.dynamodb.enhanced

import io.bluetape4k.aws.dynamodb.DynamoDb
import io.bluetape4k.aws.dynamodb.model.batchWriteItemEnhancedRequest
import io.bluetape4k.aws.dynamodb.model.writeBatchOf
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.coroutines.flow.extensions.chunked
import io.bluetape4k.support.coerce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.internal.client.ExtensionResolver
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

inline fun dynamoDbEnhancedAsyncClient(
    initializer: DynamoDbEnhancedAsyncClient.Builder.() -> Unit,
): DynamoDbEnhancedAsyncClient {
    return DynamoDbEnhancedAsyncClient.builder().apply(initializer).build()
}

fun dynamoDbEnhancedAsyncClientOf(
    client: DynamoDbAsyncClient,
    initializer: DynamoDbEnhancedAsyncClient.Builder.() -> Unit = { extensions(ExtensionResolver.defaultExtensions()) },
): DynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient {
    dynamoDbClient(client)
    initializer()
}

fun dynamoDbEnhancedAsyncClientOf(
    client: DynamoDbAsyncClient,
    vararg extensions: DynamoDbEnhancedClientExtension = ExtensionResolver.defaultExtensions().toTypedArray(),
): DynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient {
    dynamoDbClient(client)
    extensions(*extensions)
}


/**
 * Create DynamoDb Table with specific name ([tableName])
 *
 * @param T
 * @param tableName
 * @return
 */
inline fun <reified T: Any> DynamoDbEnhancedAsyncClient.table(tableName: String): DynamoDbAsyncTable<T> {
    tableName.requireNotBlank("tableName")
    return table(tableName, TableSchema.fromBean(T::class.java))
}


/**
 * 대량의 Item 을 저장할 때, [DynamoDb.MAX_BATCH_ITEM_SIZE] 만큼의 크기로 나누어 저장한다.
 *
 * @param T
 * @param itemClass entity class
 * @param table [DynamoDbAsyncTable] instance
 * @param items 저장할 item 컬렉션
 * @param chunkSize [DynamoDb.MAX_BATCH_ITEM_SIZE] 보다 작은 값을 사용해야 한다 (1~25)
 * @return [BatchWriteResult] 컬렉션
 */
suspend fun <T: Any> DynamoDbEnhancedAsyncClient.batchWriteItems(
    itemClass: Class<T>,
    table: MappedTableResource<T>,
    items: Collection<T>,
    chunkSize: Int = DynamoDb.MAX_BATCH_ITEM_SIZE,
): List<BatchWriteResult> = coroutineScope {
    val chunk = chunkSize.coerce(1, DynamoDb.MAX_BATCH_ITEM_SIZE)
    items.asFlow()
        .buffer(chunk)
        .chunked(chunk)
        .map { chunkedItems ->
            withContext(Dispatchers.IO) {
                val request = batchWriteItemEnhancedRequest {
                    val writeBatch = writeBatchOf(table, chunkedItems, itemClass)
                    addWriteBatch(writeBatch)
                }
                batchWriteItem(request).await()
            }
        }.toList()
}
