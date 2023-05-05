package io.bluetape4k.aws.dynamodb

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsAsyncClient
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsAsyncClientBuilder
import java.net.URI

inline fun DynamoDbAsyncClient(initializer: DynamoDbAsyncClientBuilder.() -> Unit): DynamoDbAsyncClient {
    return DynamoDbAsyncClient.builder().apply(initializer).build()
}

fun dynamoDbAsyncClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbAsyncClientBuilder.() -> Unit = {},
): DynamoDbAsyncClient = DynamoDbAsyncClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    initializer()
}

inline fun DynamoDbStreamsAsyncClient(
    initializer: DynamoDbStreamsAsyncClientBuilder.() -> Unit,
): DynamoDbStreamsAsyncClient {
    return DynamoDbStreamsAsyncClient.builder().apply(initializer).build()
}

fun dynamoDbStreamsAsyncClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbStreamsAsyncClientBuilder.() -> Unit = {},
): DynamoDbStreamsAsyncClient = DynamoDbStreamsAsyncClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    initializer()
}
