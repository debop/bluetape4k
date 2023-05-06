package io.bluetape4k.aws.dynamodb.model

import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.BatchExecuteStatementRequest
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest
import software.amazon.awssdk.services.dynamodb.model.BatchStatementRequest
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest
import software.amazon.awssdk.services.dynamodb.model.Capacity
import software.amazon.awssdk.services.dynamodb.model.Condition
import software.amazon.awssdk.services.dynamodb.model.ConditionCheck
import software.amazon.awssdk.services.dynamodb.model.CreateBackupRequest
import software.amazon.awssdk.services.dynamodb.model.CreateGlobalTableRequest
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.Delete
import software.amazon.awssdk.services.dynamodb.model.DeleteBackupRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.ExecuteStatementRequest
import software.amazon.awssdk.services.dynamodb.model.ExecuteTransactionRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetRecordsRequest
import software.amazon.awssdk.services.dynamodb.model.GetShardIteratorRequest
import software.amazon.awssdk.services.dynamodb.model.ListGlobalTablesRequest
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest
import software.amazon.awssdk.services.dynamodb.model.ListTagsOfResourceRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.dynamodb.model.Record
import software.amazon.awssdk.services.dynamodb.model.ReplicaSettingsUpdate
import software.amazon.awssdk.services.dynamodb.model.ReplicaUpdate
import software.amazon.awssdk.services.dynamodb.model.RestoreTableFromBackupRequest
import software.amazon.awssdk.services.dynamodb.model.RestoreTableToPointInTimeRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import software.amazon.awssdk.services.dynamodb.model.SequenceNumberRange
import software.amazon.awssdk.services.dynamodb.model.Stream
import software.amazon.awssdk.services.dynamodb.model.StreamRecord
import software.amazon.awssdk.services.dynamodb.model.StreamSpecification
import software.amazon.awssdk.services.dynamodb.model.TagResourceRequest
import software.amazon.awssdk.services.dynamodb.model.TransactGetItem
import software.amazon.awssdk.services.dynamodb.model.TransactGetItemsRequest
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest
import software.amazon.awssdk.services.dynamodb.model.UntagResourceRequest
import software.amazon.awssdk.services.dynamodb.model.Update
import software.amazon.awssdk.services.dynamodb.model.UpdateContinuousBackupsRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateContributorInsightsRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateGlobalTableRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateGlobalTableSettingsRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateTableRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest
import software.amazon.awssdk.services.dynamodb.model.WriteRequest


inline fun batchExecuteStatementRequest(initializer: BatchExecuteStatementRequest.Builder.() -> Unit): BatchExecuteStatementRequest =
    BatchExecuteStatementRequest.builder().apply(initializer).build()

inline fun batchGetItemRequest(initializer: BatchGetItemRequest.Builder.() -> Unit): BatchGetItemRequest =
    BatchGetItemRequest.builder().apply(initializer).build()

inline fun batchStatementRequest(initializer: BatchStatementRequest.Builder.() -> Unit): BatchStatementRequest =
    BatchStatementRequest.builder().apply(initializer).build()

inline fun batchWriteItemRequest(initializer: BatchWriteItemRequest.Builder.() -> Unit): BatchWriteItemRequest =
    BatchWriteItemRequest.builder().apply(initializer).build()

inline fun capacity(initializer: Capacity.Builder.() -> Unit): Capacity =
    Capacity.builder().apply(initializer).build()

inline fun conditionCheck(initializer: ConditionCheck.Builder.() -> Unit): ConditionCheck =
    ConditionCheck.builder().apply(initializer).build()

inline fun condition(initializer: Condition.Builder.() -> Unit): Condition =
    Condition.builder().apply(initializer).build()

inline fun createBackupRequest(initializer: CreateBackupRequest.Builder.() -> Unit): CreateBackupRequest =
    CreateBackupRequest.builder().apply(initializer).build()

inline fun createGlobalTableRequest(initializer: CreateGlobalTableRequest.Builder.() -> Unit): CreateGlobalTableRequest =
    CreateGlobalTableRequest.builder().apply(initializer).build()

inline fun createTableRequest(initializer: CreateTableRequest.Builder.() -> Unit): CreateTableRequest =
    CreateTableRequest.builder().apply(initializer).build()

inline fun deleteBackupRequest(initializer: DeleteBackupRequest.Builder.() -> Unit): DeleteBackupRequest =
    DeleteBackupRequest.builder().apply(initializer).build()

inline fun delete(initializer: Delete.Builder.() -> Unit): Delete =
    Delete.builder().apply(initializer).build()

inline fun deleteRequest(initializer: DeleteRequest.Builder.() -> Unit): DeleteRequest =
    DeleteRequest.builder().apply(initializer).build()

inline fun deleteTableRequest(initializer: DeleteTableRequest.Builder.() -> Unit): DeleteTableRequest =
    DeleteTableRequest.builder().apply(initializer).build()

inline fun executeStatementRequest(initializer: ExecuteStatementRequest.Builder.() -> Unit): ExecuteStatementRequest =
    ExecuteStatementRequest.builder().apply(initializer).build()

inline fun executeTransactionRequest(initializer: ExecuteTransactionRequest.Builder.() -> Unit): ExecuteTransactionRequest =
    ExecuteTransactionRequest.builder().apply(initializer).build()

inline fun getItemRequest(initializer: GetItemRequest.Builder.() -> Unit): GetItemRequest =
    GetItemRequest.builder().apply(initializer).build()

inline fun getRecordsRequest(initializer: GetRecordsRequest.Builder.() -> Unit): GetRecordsRequest =
    GetRecordsRequest.builder().apply(initializer).build()

inline fun getShardIteratorRequest(initializer: GetShardIteratorRequest.Builder.() -> Unit): GetShardIteratorRequest =
    GetShardIteratorRequest.builder().apply(initializer).build()

inline fun listGlobalTablesRequest(initializer: ListGlobalTablesRequest.Builder.() -> Unit): ListGlobalTablesRequest =
    ListGlobalTablesRequest.builder().apply(initializer).build()

inline fun listTablesRequest(initializer: ListTablesRequest.Builder.() -> Unit): ListTablesRequest =
    ListTablesRequest.builder().apply(initializer).build()

inline fun listTagsOfResourceRequest(initializer: ListTagsOfResourceRequest.Builder.() -> Unit): ListTagsOfResourceRequest =
    ListTagsOfResourceRequest.builder().apply(initializer).build()

inline fun putItemRequest(initializer: PutItemRequest.Builder.() -> Unit): PutItemRequest =
    PutItemRequest.builder().apply(initializer).build()

inline fun putRequest(initializer: PutRequest.Builder.() -> Unit): PutRequest =
    PutRequest.builder().apply(initializer).build()

fun putRequestOf(items: Map<String, AttributeValue>): PutRequest = putRequest {
    item(items)
}

inline fun queryRequest(initializer: QueryRequest.Builder.() -> Unit): QueryRequest =
    QueryRequest.builder().apply(initializer).build()

inline fun record(initializer: Record.Builder.() -> Unit): Record =
    Record.builder().apply(initializer).build()

inline fun replicaSettingsUpdate(initializer: ReplicaSettingsUpdate.Builder.() -> Unit): ReplicaSettingsUpdate =
    ReplicaSettingsUpdate.builder().apply(initializer).build()

inline fun replicaUpdate(initializer: ReplicaUpdate.Builder.() -> Unit): ReplicaUpdate =
    ReplicaUpdate.builder().apply(initializer).build()

inline fun restoreTableFromBackupRequest(
    initializer: RestoreTableFromBackupRequest.Builder.() -> Unit,
): RestoreTableFromBackupRequest =
    RestoreTableFromBackupRequest.builder().apply(initializer).build()

inline fun restoreTableToPointInTimeRequest(
    initializer: RestoreTableToPointInTimeRequest.Builder.() -> Unit,
): RestoreTableToPointInTimeRequest =
    RestoreTableToPointInTimeRequest.builder().apply(initializer).build()

inline fun scanRequest(initializer: ScanRequest.Builder.() -> Unit): ScanRequest =
    ScanRequest.builder().apply(initializer).build()

inline fun sequenceNumberRange(initializer: SequenceNumberRange.Builder.() -> Unit): SequenceNumberRange =
    SequenceNumberRange.builder().apply(initializer).build()

inline fun stream(initializer: Stream.Builder.() -> Unit): Stream =
    Stream.builder().apply(initializer).build()

inline fun streamRecord(initializer: StreamRecord.Builder.() -> Unit): StreamRecord =
    StreamRecord.builder().apply(initializer).build()

inline fun streamSpecification(initializer: StreamSpecification.Builder.() -> Unit): StreamSpecification =
    StreamSpecification.builder().apply(initializer).build()

inline fun tagResourceRequest(initializer: TagResourceRequest.Builder.() -> Unit): TagResourceRequest =
    TagResourceRequest.builder().apply(initializer).build()

inline fun transactGetItem(initializer: TransactGetItem.Builder.() -> Unit): TransactGetItem =
    TransactGetItem.builder().apply(initializer).build()

inline fun transactGetItemsRequest(
    initializer: TransactGetItemsRequest.Builder.() -> Unit,
): TransactGetItemsRequest =
    TransactGetItemsRequest.builder().apply(initializer).build()

inline fun transactWriteItem(
    initializer: TransactWriteItem.Builder.() -> Unit,
): TransactWriteItem =
    TransactWriteItem.builder().apply(initializer).build()

inline fun transactWriteItemsRequest(
    initializer: TransactWriteItemsRequest.Builder.() -> Unit,
): TransactWriteItemsRequest =
    TransactWriteItemsRequest.builder().apply(initializer).build()

inline fun untagResourceRequest(initializer: UntagResourceRequest.Builder.() -> Unit): UntagResourceRequest =
    UntagResourceRequest.builder().apply(initializer).build()

inline fun updateContinuousBackupsRequest(
    initializer: UpdateContinuousBackupsRequest.Builder.() -> Unit,
): UpdateContinuousBackupsRequest =
    UpdateContinuousBackupsRequest.builder().apply(initializer).build()

inline fun updateContributorInsightsRequest(
    initializer: UpdateContributorInsightsRequest.Builder.() -> Unit,
): UpdateContributorInsightsRequest =
    UpdateContributorInsightsRequest.builder().apply(initializer).build()

inline fun update(initializer: Update.Builder.() -> Unit): Update =
    Update.builder().apply(initializer).build()

inline fun updateGlobalTableRequest(
    initializer: UpdateGlobalTableRequest.Builder.() -> Unit,
): UpdateGlobalTableRequest =
    UpdateGlobalTableRequest.builder().apply(initializer).build()

inline fun updateGlobalTableSettingsRequest(
    initializer: UpdateGlobalTableSettingsRequest.Builder.() -> Unit,
): UpdateGlobalTableSettingsRequest =
    UpdateGlobalTableSettingsRequest.builder().apply(initializer).build()

inline fun updateItemRequest(initializer: UpdateItemRequest.Builder.() -> Unit): UpdateItemRequest =
    UpdateItemRequest.builder().apply(initializer).build()

inline fun updateTableRequest(initializer: UpdateTableRequest.Builder.() -> Unit): UpdateTableRequest =
    UpdateTableRequest.builder().apply(initializer).build()

inline fun updateTimeToLiveRequest(
    initializer: UpdateTimeToLiveRequest.Builder.() -> Unit,
): UpdateTimeToLiveRequest =
    UpdateTimeToLiveRequest.builder().apply(initializer).build()

inline fun <reified T: Any> writeBatch(
    table: MappedTableResource<T>,
    @BuilderInference initializer: WriteBatch.Builder<T>.() -> Unit,
): WriteBatch {
    return WriteBatch.builder(T::class.java)
        .mappedTableResource(table)
        .apply(initializer)
        .build()
}

inline fun <reified T: Any> writeBatchOf(
    table: MappedTableResource<T>,
    items: Collection<T>,
): WriteBatch = writeBatch<T>(table) {
    items.forEach { addPutItem(it) }
}

fun <T: Any> writeBatchOf(
    table: MappedTableResource<T>,
    items: Collection<T>,
    itemClass: Class<T>,
): WriteBatch {
    return WriteBatch.builder(itemClass)
        .mappedTableResource(table)
        .apply {
            items.forEach { addPutItem(it) }
        }
        .build()
}

inline fun writeRequest(initializer: WriteRequest.Builder.() -> Unit): WriteRequest {
    return WriteRequest.builder().apply(initializer).build()
}

fun writeRequestOf(items: Map<String, AttributeValue>): WriteRequest = writeRequest {
    putRequest(putRequestOf(items))
}


fun QueryRequest.describe(): String = buildString {
    appendLine()
    append("keyConditions: ").appendLine(keyConditions())
    append("keyConditionExpression: ").appendLine(keyConditionExpression())
    append("filter Expressin: ").appendLine(this@describe.filterExpression())

    append("expression names: ").appendLine(this@describe.expressionAttributeNames())
    append("expression values: ").appendLine(this@describe.expressionAttributeValues())
    append("attributesToGet: ").appendLine(this@describe.attributesToGet())
}
