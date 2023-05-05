package io.bluetape4k.aws.dynamodb.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitFirst
import software.amazon.awssdk.core.async.SdkPublisher
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher

fun <T: Any> SdkPublisher<Page<T>>.findFirst(): Flow<T> = flow {
    emitAll(awaitFirst().items().asFlow())
}

fun <T: Any> PagePublisher<T>.findFirst(): Flow<T> = flow {
    emitAll(awaitFirst().items().asFlow())
}

suspend fun <T: Any> SdkPublisher<Page<T>>.count(): Long {
    return awaitFirst().items().count().toLong()
}

suspend fun <T: Any> PagePublisher<T>.count(): Long {
    return awaitFirst().items().count().toLong()
}
