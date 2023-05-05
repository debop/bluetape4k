package io.bluetape4k.aws.dynamodb.schema

import io.bluetape4k.aws.dynamodb.model.provisionedThroughputOf
import io.bluetape4k.concurrent.allAsList
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import java.util.concurrent.CompletableFuture

fun <T: Any> DynamoDbAsyncTable<T>.createTable(
    readCapacityUnits: Long? = null,
    writeCapacityUnits: Long? = null,
): CompletableFuture<Void> {
    val request = CreateTableEnhancedRequest {
        provisionedThroughput(provisionedThroughputOf(readCapacityUnits, writeCapacityUnits))
    }
    return createTable(request)
}

fun <T: Any> DynamoDbAsyncTable<T>.putItems(vararg items: T): CompletableFuture<List<Void>> {
    return putItems(items.asList())
}

fun <T: Any> DynamoDbAsyncTable<T>.putItems(items: Collection<T>): CompletableFuture<List<Void>> {
    return items.map { putItem(it) }.allAsList()
}
