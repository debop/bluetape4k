package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
fun <T> Future<T>.asCompletionStage(): CompletionStage<T> = when (this) {
    is CompletionStage<*> -> this as CompletionStage<T>
    else                  -> FutureToCompletableFutureWrapper(this)
}

fun <T> Future<T>.asCompletableFuture(): CompletableFuture<T> = when (this) {
    is CompletableFuture<*> -> this as CompletableFuture<T>
    else                    -> FutureToCompletableFutureWrapper(this)
}

private class FutureToCompletableFutureWrapper<T> private constructor(
    private val future: Future<T>,
): CompletableFuture<T>() {

    companion object: KLogging() {
        operator fun <T> invoke(future: Future<T>): FutureToCompletableFutureWrapper<T> =
            FutureToCompletableFutureWrapper(future).apply {
                schedule { tryToComplete() }
            }

        private val service = Executors.newSingleThreadScheduledExecutor()

        private inline fun schedule(crossinline action: () -> Unit) {
            service.schedule({ action.invoke() }, 10, TimeUnit.NANOSECONDS)
        }
    }

    private fun tryToComplete() {
        try {
            if (future.isDone) {
                try {
                    this.complete(future.get())
                } catch (e: InterruptedException) {
                    this.completeExceptionally(e)
                } catch (e: ExecutionException) {
                    this.completeExceptionally(e.cause ?: e)
                }
                return
            }
            if (future.isCancelled) {
                this.cancel(true)
                return
            }
            schedule { tryToComplete() }
        } catch (e: Throwable) {
            log.error(e) { "Fail to wait to complete Future instance." }
            this.completeExceptionally(e.cause ?: e)
        }
    }
}
