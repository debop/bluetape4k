package io.bluetape4k.aws.dynamodb

import io.bluetape4k.aws.http.SdkHttpClientProvider
import io.bluetape4k.utils.ShutdownQueue
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClientBuilder
import java.net.URI

inline fun dynamoDbClient(
    initializer: DynamoDbClientBuilder.() -> Unit,
): DynamoDbClient {
    return DynamoDbClient.builder().apply(initializer).build()
        .apply {
            ShutdownQueue.register(this)
        }
}

fun dynamoDbClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbClientBuilder.() -> Unit = {},
): DynamoDbClient = dynamoDbClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    httpClient(SdkHttpClientProvider.Apache.apacheHttpClient)

    initializer()
}


inline fun dynamoDbStreamsClient(
    initializer: DynamoDbStreamsClientBuilder.() -> Unit,
): DynamoDbStreamsClient {
    return DynamoDbStreamsClient.builder().apply(initializer).build()
}

fun dynamoDbStreamsClientOf(
    endpoint: URI,
    region: Region,
    credentialsProvider: AwsCredentialsProvider,
    initializer: DynamoDbStreamsClientBuilder.() -> Unit = {},
): DynamoDbStreamsClient = dynamoDbStreamsClient {
    endpointOverride(endpoint)
    region(region)
    credentialsProvider(credentialsProvider)
    httpClient(SdkHttpClientProvider.Apache.apacheHttpClient)

    initializer()
}
