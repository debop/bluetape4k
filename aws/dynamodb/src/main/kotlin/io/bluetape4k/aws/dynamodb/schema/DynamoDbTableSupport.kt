package io.bluetape4k.aws.dynamodb.schema

import io.bluetape4k.aws.dynamodb.model.provisionedThroughputOf
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedLocalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput

fun <T: Any> DynamoDbTable<T>.createTable(
    readCapacityUnits: Long? = null,
    writeCapacityUnits: Long? = null,
) {
    val request = CreateTableEnhancedRequest {
        provisionedThroughput(provisionedThroughputOf(readCapacityUnits, writeCapacityUnits))
    }

    createTable(request)
}

fun <T: Any> DynamoDbTable<T>.putItems(vararg items: T) {
    putItems(items.asList())
}

fun <T: Any> DynamoDbTable<T>.putItems(items: Collection<T>) {
    items.forEach { putItem(it) }
}

inline fun CreateTableEnhancedRequest(
    initializer: CreateTableEnhancedRequest.Builder.() -> Unit,
): CreateTableEnhancedRequest {
    return CreateTableEnhancedRequest.builder().apply(initializer).build()
}

fun createTableEnhancedRequestOf(
    provisionedThroughput: ProvisionedThroughput? = null,
    localSecondaryIndices: Collection<EnhancedLocalSecondaryIndex>? = null,
    globalSecondaryIndices: Collection<EnhancedGlobalSecondaryIndex>? = null,
): CreateTableEnhancedRequest = CreateTableEnhancedRequest {
    provisionedThroughput(provisionedThroughput)
    localSecondaryIndices(localSecondaryIndices)
    globalSecondaryIndices(globalSecondaryIndices)
}
