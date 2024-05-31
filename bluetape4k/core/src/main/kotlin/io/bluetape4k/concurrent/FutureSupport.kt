package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

@Suppress("UNCHECKED_CAST")
fun <T> Future<T>.asCompletionStage(): CompletionStage<T> = when (this@asCompletionStage) {
    is CompletionStage<*> -> this@asCompletionStage as CompletionStage<T>
    else                  -> FutureToCompletableFutureWrapper(this)
}

fun <T> Future<T>.asCompletableFuture(): CompletableFuture<T> = when (this@asCompletableFuture) {
    is CompletableFuture<*> -> this@asCompletableFuture as CompletableFuture<T>
    else                    -> FutureToCompletableFutureWrapper(this)
}

private class FutureToCompletableFutureWrapper<T> private constructor(
    private val future: Future<T>,
): CompletableFuture<T>() {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T> invoke(future: Future<T>): FutureToCompletableFutureWrapper<T> =
            FutureToCompletableFutureWrapper(future).apply {
                schedule { tryToComplete() }
            }
    }

    private val service = Executors.newSingleThreadScheduledExecutor()

    private inline fun schedule(crossinline action: () -> Unit) {
        service.schedule({ action() }, 100, TimeUnit.NANOSECONDS)
    }

    private fun tryToComplete() {
        try {
            if (future.isDone) {
                try {
                    this.complete(future.get())
                } catch (e: InterruptedException) {
                    this.completeExceptionally(e.cause ?: e)
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

    override fun whenComplete(action: BiConsumer<in T, in Throwable>?): CompletableFuture<T> {
        return super.whenComplete(action).apply {
            service.shutdown()
        }
    }
}
