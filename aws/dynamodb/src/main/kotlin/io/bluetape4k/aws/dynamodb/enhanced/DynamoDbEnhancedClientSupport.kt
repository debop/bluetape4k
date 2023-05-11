package io.bluetape4k.aws.dynamodb.enhanced

import io.bluetape4k.core.requireNotBlank
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.internal.client.ExtensionResolver
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

inline fun dynamoDbEnhancedClient(initializer: DynamoDbEnhancedClient.Builder.() -> Unit): DynamoDbEnhancedClient {
    return DynamoDbEnhancedClient.builder().apply(initializer).build()
}

fun dynamoDbEnhancedClientOf(
    client: DynamoDbClient,
    initializer: DynamoDbEnhancedClient.Builder.() -> Unit = { extensions(ExtensionResolver.defaultExtensions()) },
): DynamoDbEnhancedClient = dynamoDbEnhancedClient {
    dynamoDbClient(client)
    initializer()
}

fun dynamoDbEnhancedClientOf(
    client: DynamoDbClient,
    vararg extensions: DynamoDbEnhancedClientExtension = ExtensionResolver.defaultExtensions().toTypedArray(),
): DynamoDbEnhancedClient = dynamoDbEnhancedClient {
    dynamoDbClient(client)
    extensions(*extensions)
}

/**
 * Create DynamoDb Table with specific name ([tableName])
 *
 * @param T entity type
 * @param tableName table name
 * @return [DynamoDbTable] instance
 */
inline fun <reified T: Any> DynamoDbEnhancedClient.table(tableName: String): DynamoDbTable<T> {
    tableName.requireNotBlank("tableName")
    return table(tableName, TableSchema.fromBean(T::class.java))
}
