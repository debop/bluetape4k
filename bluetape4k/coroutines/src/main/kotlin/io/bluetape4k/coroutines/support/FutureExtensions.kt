package io.bluetape4k.coroutines.support

import io.bluetape4k.concurrent.asCompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import kotlinx.coroutines.future.await
import kotlin.coroutines.cancellation.CancellationException

@Suppress("UNCHECKED_CAST")
suspend fun <T> Future<T>.awaitSuspending(): T = when (this) {
    is CompletionStage<*> -> await() as T
    else ->
        when {
            isDone -> {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    get() as T
                } catch (e: ExecutionException) {
                    throw e.cause ?: e
                }
            }

            isCancelled -> throw CancellationException()
            else -> this.asCompletableFuture().await()
        }
}
