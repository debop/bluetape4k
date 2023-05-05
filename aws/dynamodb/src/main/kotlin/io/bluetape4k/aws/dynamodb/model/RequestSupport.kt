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


inline fun BatchExecuteStatementRequest(setup: BatchExecuteStatementRequest.Builder.() -> Unit): BatchExecuteStatementRequest =
    BatchExecuteStatementRequest.builder().apply(setup).build()

inline fun BatchGetItemRequest(block: BatchGetItemRequest.Builder.() -> Unit): BatchGetItemRequest =
    BatchGetItemRequest.builder().apply(block).build()

inline fun BatchStatementRequest(setup: BatchStatementRequest.Builder.() -> Unit): BatchStatementRequest =
    BatchStatementRequest.builder().apply(setup).build()

inline fun BatchWriteItemRequest(block: BatchWriteItemRequest.Builder.() -> Unit): BatchWriteItemRequest =
    BatchWriteItemRequest.builder().apply(block).build()

inline fun Capacity(block: Capacity.Builder.() -> Unit): Capacity =
    Capacity.builder().apply(block).build()

inline fun ConditionCheck(block: ConditionCheck.Builder.() -> Unit): ConditionCheck =
    ConditionCheck.builder().apply(block).build()

inline fun Condition(block: Condition.Builder.() -> Unit): Condition =
    Condition.builder().apply(block).build()

inline fun CreateBackupRequest(block: CreateBackupRequest.Builder.() -> Unit): CreateBackupRequest =
    CreateBackupRequest.builder().apply(block).build()

inline fun CreateGlobalTableRequest(block: CreateGlobalTableRequest.Builder.() -> Unit): CreateGlobalTableRequest =
    CreateGlobalTableRequest.builder().apply(block).build()

inline fun CreateTableRequest(block: CreateTableRequest.Builder.() -> Unit): CreateTableRequest =
    CreateTableRequest.builder().apply(block).build()

inline fun DeleteBackupRequest(block: DeleteBackupRequest.Builder.() -> Unit): DeleteBackupRequest =
    DeleteBackupRequest.builder().apply(block).build()

inline fun Delete(block: Delete.Builder.() -> Unit): Delete =
    Delete.builder().apply(block).build()

inline fun DeleteRequest(block: DeleteRequest.Builder.() -> Unit): DeleteRequest =
    DeleteRequest.builder().apply(block).build()

inline fun DeleteTableRequest(block: DeleteTableRequest.Builder.() -> Unit): DeleteTableRequest =
    DeleteTableRequest.builder().apply(block).build()

inline fun ExecuteStatementRequest(setup: ExecuteStatementRequest.Builder.() -> Unit): ExecuteStatementRequest =
    ExecuteStatementRequest.builder().apply(setup).build()

inline fun ExecuteTransactionRequest(setup: ExecuteTransactionRequest.Builder.() -> Unit): ExecuteTransactionRequest =
    ExecuteTransactionRequest.builder().apply(setup).build()

inline fun GetItemRequest(setup: GetItemRequest.Builder.() -> Unit): GetItemRequest =
    GetItemRequest.builder().apply(setup).build()

inline fun GetRecordsRequest(setup: GetRecordsRequest.Builder.() -> Unit): GetRecordsRequest =
    GetRecordsRequest.builder().apply(setup).build()

inline fun GetShardIteratorRequest(setup: GetShardIteratorRequest.Builder.() -> Unit): GetShardIteratorRequest =
    GetShardIteratorRequest.builder().apply(setup).build()

inline fun ListGlobalTablesRequest(block: ListGlobalTablesRequest.Builder.() -> Unit): ListGlobalTablesRequest =
    ListGlobalTablesRequest.builder().apply(block).build()

inline fun ListTablesRequest(block: ListTablesRequest.Builder.() -> Unit): ListTablesRequest =
    ListTablesRequest.builder().apply(block).build()

inline fun ListTagsOfResourceRequest(block: ListTagsOfResourceRequest.Builder.() -> Unit): ListTagsOfResourceRequest =
    ListTagsOfResourceRequest.builder().apply(block).build()

inline fun PutItemRequest(block: PutItemRequest.Builder.() -> Unit): PutItemRequest =
    PutItemRequest.builder().apply(block).build()

inline fun PutRequest(block: PutRequest.Builder.() -> Unit): PutRequest =
    PutRequest.builder().apply(block).build()

fun putRequestOf(items: Map<String, AttributeValue>): PutRequest = PutRequest {
    item(items)
}

inline fun QueryRequest(block: QueryRequest.Builder.() -> Unit): QueryRequest =
    QueryRequest.builder().apply(block).build()

inline fun Record(block: Record.Builder.() -> Unit): Record =
    Record.builder().apply(block).build()

inline fun ReplicaSettingsUpdate(block: ReplicaSettingsUpdate.Builder.() -> Unit): ReplicaSettingsUpdate =
    ReplicaSettingsUpdate.builder().apply(block).build()

inline fun ReplicaUpdate(block: ReplicaUpdate.Builder.() -> Unit): ReplicaUpdate =
    ReplicaUpdate.builder().apply(block).build()

inline fun RestoreTableFromBackupRequest(block: RestoreTableFromBackupRequest.Builder.() -> Unit): RestoreTableFromBackupRequest =
    RestoreTableFromBackupRequest.builder().apply(block).build()

inline fun RestoreTableToPointInTimeRequest(block: RestoreTableToPointInTimeRequest.Builder.() -> Unit): RestoreTableToPointInTimeRequest =
    RestoreTableToPointInTimeRequest.builder().apply(block).build()

inline fun ScanRequest(block: ScanRequest.Builder.() -> Unit): ScanRequest =
    ScanRequest.builder().apply(block).build()

inline fun SequenceNumberRange(block: SequenceNumberRange.Builder.() -> Unit): SequenceNumberRange =
    SequenceNumberRange.builder().apply(block).build()

inline fun Stream(block: Stream.Builder.() -> Unit): Stream =
    Stream.builder().apply(block).build()

inline fun StreamRecord(block: StreamRecord.Builder.() -> Unit): StreamRecord =
    StreamRecord.builder().apply(block).build()

inline fun StreamSpecification(block: StreamSpecification.Builder.() -> Unit): StreamSpecification =
    StreamSpecification.builder().apply(block).build()

inline fun TagResourceRequest(block: TagResourceRequest.Builder.() -> Unit): TagResourceRequest =
    TagResourceRequest.builder().apply(block).build()

inline fun TransactGetItem(block: TransactGetItem.Builder.() -> Unit): TransactGetItem =
    TransactGetItem.builder().apply(block).build()

inline fun TransactGetItemsRequest(block: TransactGetItemsRequest.Builder.() -> Unit): TransactGetItemsRequest =
    TransactGetItemsRequest.builder().apply(block).build()

inline fun TransactWriteItem(block: TransactWriteItem.Builder.() -> Unit): TransactWriteItem =
    TransactWriteItem.builder().apply(block).build()

inline fun TransactWriteItemsRequest(block: TransactWriteItemsRequest.Builder.() -> Unit): TransactWriteItemsRequest =
    TransactWriteItemsRequest.builder().apply(block).build()

inline fun UntagResourceRequest(block: UntagResourceRequest.Builder.() -> Unit): UntagResourceRequest =
    UntagResourceRequest.builder().apply(block).build()

inline fun UpdateContinuousBackupsRequest(block: UpdateContinuousBackupsRequest.Builder.() -> Unit): UpdateContinuousBackupsRequest =
    UpdateContinuousBackupsRequest.builder().apply(block).build()

inline fun UpdateContributorInsightsRequest(block: UpdateContributorInsightsRequest.Builder.() -> Unit): UpdateContributorInsightsRequest =
    UpdateContributorInsightsRequest.builder().apply(block).build()

inline fun Update(block: Update.Builder.() -> Unit): Update =
    Update.builder().apply(block).build()

inline fun UpdateGlobalTableRequest(block: UpdateGlobalTableRequest.Builder.() -> Unit): UpdateGlobalTableRequest =
    UpdateGlobalTableRequest.builder().apply(block).build()

inline fun UpdateGlobalTableSettingsRequest(block: UpdateGlobalTableSettingsRequest.Builder.() -> Unit): UpdateGlobalTableSettingsRequest =
    UpdateGlobalTableSettingsRequest.builder().apply(block).build()

inline fun UpdateItemRequest(block: UpdateItemRequest.Builder.() -> Unit): UpdateItemRequest =
    UpdateItemRequest.builder().apply(block).build()

inline fun UpdateTableRequest(block: UpdateTableRequest.Builder.() -> Unit): UpdateTableRequest =
    UpdateTableRequest.builder().apply(block).build()

inline fun UpdateTimeToLiveRequest(block: UpdateTimeToLiveRequest.Builder.() -> Unit): UpdateTimeToLiveRequest =
    UpdateTimeToLiveRequest.builder().apply(block).build()


inline fun <reified T: Any> WriteBatch(
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
): WriteBatch = WriteBatch<T>(table) {
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

inline fun WriteRequest(initializer: WriteRequest.Builder.() -> Unit): WriteRequest {
    return WriteRequest.builder().apply(initializer).build()
}

fun writeRequestOf(
    items: Map<String, AttributeValue>,
): WriteRequest = WriteRequest {
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
