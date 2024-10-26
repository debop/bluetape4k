package io.bluetape4k.aws.dynamodb

import io.bluetape4k.aws.http.SdkAsyncHttpClientProvider
import io.bluetape4k.utils.ShutdownQueue
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsAsyncClient
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsAsyncClientBuilder
import java.net.URI

inline fun dynamoDbAsyncClient(
    initializer: DynamoDbAsyncClientBuilder.() -> Unit,
): DynamoDbAsyncClient {
    return DynamoDbAsyncClient.builder().apply(initializer).build()
        .apply {
            ShutdownQueue.register(this)
        }
}

inline fun dynamoDbAsyncClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbAsyncClientBuilder.() -> Unit = {},
): DynamoDbAsyncClient = dynamoDbAsyncClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    httpClient(SdkAsyncHttpClientProvider.Netty.nettyNioAsyncHttpClient)
    initializer()
}

inline fun dynamoDbStreamsAsyncClient(
    initializer: DynamoDbStreamsAsyncClientBuilder.() -> Unit,
): DynamoDbStreamsAsyncClient {
    return DynamoDbStreamsAsyncClient.builder().apply(initializer).build()
}

inline fun dynamoDbStreamsAsyncClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbStreamsAsyncClientBuilder.() -> Unit = {},
): DynamoDbStreamsAsyncClient = dynamoDbStreamsAsyncClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    httpClient(SdkAsyncHttpClientProvider.Netty.nettyNioAsyncHttpClient)
    initializer()
}
