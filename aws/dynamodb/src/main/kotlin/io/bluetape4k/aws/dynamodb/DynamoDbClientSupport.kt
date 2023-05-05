package io.bluetape4k.aws.dynamodb

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClientBuilder
import java.net.URI

inline fun DynamoDbClient(initializer: DynamoDbClientBuilder.() -> Unit): DynamoDbClient {
    return DynamoDbClient.builder().apply(initializer).build()
}

fun dynamoDbClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbClientBuilder.() -> Unit = {},
): DynamoDbClient = DynamoDbClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    initializer()
}


inline fun DynamoDbStreamsClient(initializer: DynamoDbStreamsClientBuilder.() -> Unit): DynamoDbStreamsClient {
    return DynamoDbStreamsClient.builder().apply(initializer).build()
}

fun dynamoDbStreamsClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbStreamsClientBuilder.() -> Unit = {},
): DynamoDbStreamsClient = DynamoDbStreamsClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    initializer()
}