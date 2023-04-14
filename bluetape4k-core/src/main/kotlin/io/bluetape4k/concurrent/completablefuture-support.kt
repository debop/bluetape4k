package io.bluetape4k.concurrent

import java.time.Duration
import java.util.concurrent.*


/**
 * 지정한 block을 비동기로 실행하고, [CompletableFuture]를 반환합니다.
 * @param executor [block]을 수행할 [Executor]
 * @param block 비동기로 수행할 코드 블럭
 * @return CompletableFuture<V>
 */
@JvmOverloads
inline fun <V> futureOf(executor: Executor = ForkJoinExecutor, crossinline block: () -> V): CompletableFuture<V> =
    CompletableFuture.supplyAsync({ block() }, executor)

/**
 * 실행을 동기 방식으로 수행하지만, [CompletableFuture]로 변환합니다.
 *
 * @param block 비동기로 수행할 코드 블럭
 * @return CompletableFuture<V>
 */
inline fun <V> immediateFutureOf(crossinline block: () -> V): CompletableFuture<V> =
    futureOf(DirectExecutor, block)

fun <V> completableFutureOf(value: V): CompletableFuture<V> = CompletableFuture.completedFuture(value)

fun <V> failedCompletableFutureOf(cause: Throwable): CompletableFuture<V> = CompletableFuture.failedFuture(cause)

/**
 * 비동기 작업 수행에 timeout 을 적용합니다.
 *
 * ```
 * val future:CompletableFuture<Unit> = futureWithTimeout(Duration.ofSeconds(1)) {
 *   // 1초 이내에 종료되지 않으면 실패로 간주한다
 *   // async task()
 * }
 * ```
 *
 * @param V
 * @param timeout 최대 수행 시간
 * @param block 수행할 코드 블럭
 * @return [block]의 실행 결과, [timeout] 시간 내에 종료되지 않으면 null 을 반환하는 [CompletableFuture] 인스턴스
 */
inline fun <V> futureWithTimeout(timeout: Duration, crossinline block: () -> V): CompletableFuture<V> {
    return futureWithTimeout(timeout.toMillis(), block)
}

/**
 * 비동기 작업 수행에 timeout 을 적용합니다.
 *
 * ```
 * val future:CompletableFuture<Unit> = futureWithTimeout(1000L) {
 *   // 1초 이내에 종료되지 않으면 실패로 간주한다
 *   // async task()
 * }
 * ```
 *
 * @param V
 * @param timeout 최대 수행 시간
 * @param block 수행할 코드 블럭
 * @return [block]의 실행 결과, [timeout] 시간 내에 종료되지 않으면 실패했음을 나타내는 [CompletableFuture] 인스턴스
 */
inline fun <V> futureWithTimeout(timeoutMillis: Long = 1000L, crossinline block: () -> V): CompletableFuture<V> {
    val executor = Executors.newSingleThreadExecutor()
    return CompletableFuture
        .supplyAsync({ block() }, executor)
        .orTimeout(timeoutMillis.coerceAtLeast(10L), TimeUnit.MILLISECONDS)
        .whenCompleteAsync { _, _ ->
            // action을 즉시 강제 종료 시킨다
            executor.shutdownNow()
        }
}


/**
 * [CompletableFuture]가 완료되면 결과 값을 [mapper]를 이용하여 변환한 값을 반환하도록 합니다.
 *
 * ```
 * val name: Long = completableFutureOf("1234").map { it.toLong() }
 * ```
 */
inline fun <V, R> CompletableFuture<V>.map(
    executor: Executor = ForkJoinExecutor,
    crossinline mapper: (value: V) -> R,
): CompletableFuture<R> =
    thenApplyAsync({ mapper(it) }, executor)


inline fun <V, R> CompletableFuture<V>.flatMap(
    executor: Executor = ForkJoinExecutor,
    crossinline mapper: (value: V) -> CompletionStage<R>,
): CompletableFuture<R> =
    thenComposeAsync({ mapper(it) }, executor)

inline fun <V, R> CompletableFuture<V>.mapResult(
    executor: Executor = ForkJoinExecutor,
    crossinline handler: (value: V?, error: Throwable?) -> R,
): CompletableFuture<R> =
    handleAsync({ v, e -> handler(v, e) }, executor)

fun <V> CompletableFuture<out CompletableFuture<V>>.flatten(executor: Executor = ForkJoinExecutor): CompletableFuture<V> =
    flatMap(executor) { it }

fun <V> CompletableFuture<out CompletableFuture<V>>.dereference(executor: Executor = ForkJoinExecutor): CompletableFuture<V> =
    flatMap(executor) { it }

fun <V> CompletableFuture<V>.wrap(executor: Executor = ForkJoinExecutor): CompletableFuture<CompletableFuture<V>> =
    map(executor) { CompletableFuture.completedFuture(it) }

inline fun <V> CompletableFuture<V>.filter(
    executor: Executor = ForkJoinExecutor,
    crossinline predicate: (value: V) -> Boolean,
): CompletableFuture<V> {
    return map(executor) {
        if (predicate(it)) it
        else throw NoSuchElementException("CompletableFuture.filter predicate is not satisfied. result=$it")
    }
}

inline fun <A, B, R> CompletableFuture<A>.zip(
    other: CompletionStage<out B>,
    executor: Executor = ForkJoinExecutor,
    crossinline zipper: (A, B) -> R,
): CompletableFuture<R> =
    thenCombineAsync(other, { a, b -> zipper(a, b) }, executor)

fun <A, B> CompletableFuture<A>.zip(
    other: CompletionStage<out B>,
    executor: Executor = ForkJoinExecutor,
): CompletableFuture<Pair<A, B>> =
    thenCombineAsync(other, { a, b -> a to b }, executor)


/**
 * 예외가 발생하면 보상하는 함수를 통한 결과 값을 반환하도록 합니다.
 *
 * @receiver CompletableFuture<V>
 * @param action Function1<Throwable, T>
 * @return CompletableFuture<V>
 */
inline fun <V> CompletableFuture<V>.recover(crossinline action: (error: Throwable) -> V): CompletableFuture<V> =
    exceptionally { e -> action(e.cause ?: e) }


/**
 * 예외가 발생하면 보상하는 함수를 통한 결과 값을 반환하도록 합니다.
 *
 * @see [CompletableFuture.fallbackTo]
 *
 * @receiver CompletableFuture<V>
 * @param executor Executor
 * @param action Function1<Throwable, CompletableFuture<V>>
 * @return CompletableFuture<V>
 */
inline fun <V> CompletableFuture<V>.recoverWith(
    executor: Executor = ForkJoinExecutor,
    crossinline action: (error: Throwable) -> CompletableFuture<V>,
): CompletableFuture<V> {
    val promise = CompletableFuture<V>()

    onComplete(executor,
        successHandler = { result -> promise.complete(result) },
        failureHandler = {
            action(it).onComplete(executor,
                successHandler = { result -> promise.complete(result) },
                failureHandler = { error -> promise.completeExceptionally(error) })
        })

    return promise
}

/**
 * [CompletableFuture]가 예외를 발생 시킬 시에, 예외를 매핑한 후 예외를 발생시킵니다.
 *
 * @receiver CompletableFuture<V>
 * @param mapper Function1<E, Throwable>
 * @return CompletableFuture<V>
 */
inline fun <V, reified E : Throwable> CompletableFuture<V>.mapError(crossinline mapper: (error: E) -> Throwable): CompletableFuture<V> =
    exceptionally {
        when (val error = it.cause ?: it) {
            is E -> throw mapper(error)
            else -> throw error
        }
    }

/**
 * 예외가 발생하면 보상하는 함수를 통한 결과 값을 반환하도록 합니다.
 *
 * @see [CompletableFuture.recover]
 * @see [CompletableFuture.recoverWith]
 *
 * @receiver CompletableFuture<V>
 * @param executor Executor
 * @param fallback 복구 코드
 * @return CompletableFuture<V>
 */
inline fun <V> CompletableFuture<V>.fallbackTo(
    executor: Executor = ForkJoinExecutor,
    crossinline fallback: () -> CompletableFuture<V>,
): CompletableFuture<V> =
    recoverWith(executor) { fallback() }


inline fun <V> CompletableFuture<V>.onFailure(
    executor: Executor = ForkJoinExecutor,
    crossinline errorHandler: (error: Throwable) -> Unit,
): CompletableFuture<V> =
    whenCompleteAsync(
        { _, error ->
            if (error != null) {
                errorHandler(error.cause ?: error)
            }
        },
        executor
    )

inline fun <V> CompletableFuture<V>.onSuccess(
    executor: Executor = ForkJoinExecutor,
    crossinline successHandler: (result: V) -> Unit,
): CompletableFuture<V> =
    whenCompleteAsync(
        { result, error -> if (error == null) successHandler(result) },
        executor
    )


inline fun <V> CompletableFuture<V>.onComplete(
    executor: Executor = ForkJoinExecutor,
    crossinline successHandler: (result: V) -> Unit,
    crossinline failureHandler: (error: Throwable) -> Unit,
): CompletableFuture<V> =
    whenCompleteAsync(
        { result, error ->
            if (error != null) failureHandler(error.cause ?: error)
            else successHandler(result)
        },
        executor
    )

inline fun <V> CompletableFuture<V>.onComplete(
    executor: Executor = ForkJoinExecutor,
    crossinline completionHandler: (result: V?, error: Throwable?) -> Unit,
): CompletableFuture<V> =
    whenCompleteAsync(
        { result, error -> completionHandler(result, error) },
        executor
    )


val <V> CompletableFuture<V>.isFailed: Boolean get() = this.isCompletedExceptionally
val <V> CompletableFuture<V>.isSuccess: Boolean get() = this.isDone

fun <V> CompletableFuture<V>.join(duration: Duration): V {
    return try {
        get(duration.toNanos(), TimeUnit.NANOSECONDS)
    } catch (e: Exception) {
        //        if (e is InterruptedException || e is ExecutionException || e is TimeoutException) null
        //        else throw e
        throw e.cause ?: e
    }
}

fun <V> CompletableFuture<V>.join(duration: Duration, defaultValue: V): V =
    runCatching { join(duration) ?: defaultValue }.getOrDefault(defaultValue)

fun <V> CompletableFuture<V>.joinOrNull(duration: Duration): V? =
    runCatching { get(duration.toNanos(), TimeUnit.NANOSECONDS) }.getOrNull()
