package io.bluetape4k.aws.dynamodb

import io.bluetape4k.aws.dynamodb.model.batchWriteItemRequest
import io.bluetape4k.aws.dynamodb.model.writeRequest
import io.bluetape4k.aws.dynamodb.model.writeRequestOf
import io.bluetape4k.logging.KLogging
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.WriteRequest
import kotlin.coroutines.CoroutineContext

class DynamoDbBatchExecutor<T: Any>(
    private val dynamoDB: DynamoDbClient,
    private val retry: Retry,
): CoroutineScope {

    companion object: KLogging() {
        /**
         * DynamoDB의 BatchWriteItem 은 Batch당 최대 25개의 Item만 허용합니다.
         */
        const val BATCH_SIZE: Int = 25

        operator fun <T: Any> invoke(
            dynamoDB: DynamoDbClient = DynamoDbClient.create(),
            retry: Retry = Retry.ofDefaults("dynamo-batch"),
        ): DynamoDbBatchExecutor<T> {
            return DynamoDbBatchExecutor(dynamoDB, retry)
        }
    }

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    data class TableItemTuple(val tableName: String, val writeRequest: WriteRequest)
    data class RetryablePut(val attempt: Int, val items: List<TableItemTuple>)

    /**
     * [tableName] table에서 [items] 를 삭제하는 작업을 Batch로 수행합니다.
     *
     * @param tableName  Table name
     * @param items      삭제할 Items
     * @param primaryKeySelector primary key selector
     */
    suspend fun delete(tableName: String, items: List<T>, primaryKeySelector: (T) -> Map<String, AttributeValue>) {
        val writeRequests = items
            .map { item ->
                writeRequest {
                    io.bluetape4k.aws.dynamodb.model.deleteRequest { key(primaryKeySelector(item)) }
                }
            }
            .map { TableItemTuple(tableName, it) }

        writeRequests
            .chunked(BATCH_SIZE)
            .forEach {
                executeBatchPersist(it)
            }
    }

    suspend fun persist(tableName: String, items: List<T>, mapper: DynamoItemMapper<T>) {
        val writeItems = items.buildWriteRequest(mapper)
        persist(writeItems.map { TableItemTuple(tableName, it) })
    }

    suspend fun persist(tableName: String, items: List<Map<String, AttributeValue>>) {
        val writeItems = items.map { writeRequestOf(it) }
        persist(writeItems.map { TableItemTuple(tableName, it) })
    }

    suspend fun persist(writeItems: List<TableItemTuple>) {
        writeItems
            .chunked(BATCH_SIZE)
            .asFlow()
            .buffer()
            .onEach { executeBatchPersist(it) }
            .collect()
    }

    private suspend fun executeBatchPersist(writeList: List<TableItemTuple>) {
        retry.executeSuspendFunction {
            batchPersist(writeList)
        }
    }

    private suspend fun batchPersist(writeList: List<TableItemTuple>) {
        val requestItems = writeList.groupBy({ it.tableName }, { it.writeRequest })
        val batchRequest = batchWriteItemRequest { requestItems(requestItems) }

        // Non-Blocking 으로 저장 작업을 수행하기 위해서
        val result = withContext(coroutineContext) {
            // DynamoDB의 Batch 쓰기 작업
            dynamoDB.batchWriteItem(batchRequest)
        }

        // Partial failure
        if (result.unprocessedItems().isNotEmpty()) {
            val unprocessedWriteList = buildWriteLists(result.unprocessedItems())
            batchPersist(unprocessedWriteList)
        }
    }

    private fun buildWriteLists(items: Map<String, List<WriteRequest>>): List<TableItemTuple> {
        return items.entries
            .flatMap { entry ->
                entry.value.map { TableItemTuple(entry.key, it) }
            }
    }
}
