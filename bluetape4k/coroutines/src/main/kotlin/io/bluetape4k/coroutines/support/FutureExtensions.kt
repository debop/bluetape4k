package io.bluetape4k.coroutines.support

import io.bluetape4k.concurrent.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

@Deprecated("use coAwait()", replaceWith = ReplaceWith("coAwait()"))
@Suppress("UNCHECKED_CAST")
suspend fun <T> Future<T>.awaitSuspending(): T = when (this) {
    is CompletionStage<*> -> await() as T
    else                  ->
        when {
            isDone      -> {
                try {
                    get() as T
                } catch (e: ExecutionException) {
                    throw e.cause ?: e
                }
            }

            isCancelled -> throw CancellationException()
            else        -> this.asCompletableFuture().await()
        }
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> Future<T>.coAwait(): T = when (this) {
    is CompletionStage<*> ->
        await() as T

    else                  ->
        when {
            isDone      -> {
                try {
                    withContext(coroutineContext) { get() }
                } catch (e: ExecutionException) {
                    throw e.cause ?: e
                }
            }

            isCancelled -> throw CancellationException()
            else        -> this.asCompletableFuture().await()
        }
}
