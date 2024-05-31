package io.bluetape4k.concurrent

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.StructuredTaskScope
import java.util.function.BiFunction


/**
 * [CompletionStage]의 예외정보를 가져온다.
 * 성공적으로 완료된 CompletionStage라면 예외가 발생한다.
 */
fun <T> CompletionStage<T>.getException(): Throwable? {
    val future = this.toCompletableFuture()
    if (!future.isCompletedExceptionally) {
        error("$this was not completed exceptionally.")
    }
    return try {
        future.join()
        null
    } catch (e: CompletionException) {
        e.cause
    }
}

/**
 * [CompletionStage]의 컬렉션을 `CompletableFuture<List<*>>` 로 변환합니다.
 *
 * @receiver Iterable<CompletionStage<out Any>>
 * @param executor Executor
 * @return CompletableFuture<List<*>>
 */
fun Iterable<CompletionStage<out Any>>.sequence(executor: Executor = ForkJoinExecutor): CompletableFuture<List<*>> {
    val initial = completableFutureOf(mutableListOf<Any>())
    return fold(initial) { futureAcc, future ->
        futureAcc.zip(future, executor) { acc, result ->
            acc.apply { add(result) }
        }
    }.map(executor) { it.toList() }
}

/**
 * Generic 수형의 [CompletionStage] 컬렉션을 [List]의 [CompletableFuture]로 변환합니다.
 *
 * ```
 * val futures: List<CompletableFuture<Int>> = listOf(...)
 * val results: CompletableFuture<List<Int>> = futures.sequence()
 * ```
 *
 * @param executor Executor (default: ForkJoinExecutor)
 * @return `CompletableFuture<List<T>>`
 */
fun <T> Collection<CompletionStage<out T>>.sequence(executor: Executor = ForkJoinExecutor): CompletableFuture<List<T>> {
    val initial = completableFutureOf(mutableListOf<T>())
    return fold(initial) { futureAcc, future ->
        futureAcc.zip(future, executor) { acc, result ->
            acc.apply { add(result) }
        }
    }.map(executor) { it.toList() }
}

/**
 * [CompletionStage]의 컬렉션을 `CompletableFuture<List<*>>` 로 변환합니다.
 *
 * @receiver Iterable<CompletionStage<out Any>>
 * @param executor Executor
 * @return CompletableFuture<List<*>>
 */
fun Iterable<CompletionStage<out Any>>.allAsList(executor: Executor = ForkJoinExecutor): CompletableFuture<List<*>> =
    sequence(executor)


/**
 * Generic 수형의 [CompletionStage] 컬렉션을 [List]의 [CompletableFuture]로 변환합니다.
 *
 * ```
 * val futures: List<CompletableFuture<Int>> = listOf(...)
 * val results: CompletableFuture<List<Int>> = futures.allAsList()
 * ```
 *
 * @param executor Executor (default: ForkJoinExecutor)
 * @return `CompletableFuture<List<T>>`
 */
fun <T> List<CompletionStage<out T>>.allAsList(executor: Executor = ForkJoinExecutor): CompletableFuture<List<T>> =
    sequence(executor)

/**
 * Generic 수형의 [CompletionStage] 컬렉션의 예외가 있는 경우 [defaultValueMapper]로 매핑하여 [List]의 [CompletableFuture]로 변환합니다.
 *
 * ```
 * val futures: List<CompletableFuture<Int>> = listOf(...)
 * val results: CompletableFuture<List<Int>> = futures.successfulAsList { 0 }
 * ```
 *
 * @param executor Executor (default: ForkJoinExecutor)
 * @return `CompletableFuture<List<T>>`
 */
fun <T> List<CompletionStage<T>>.successfulAsList(
    executor: Executor = ForkJoinExecutor,
    defaultValueMapper: (Throwable) -> T,
): CompletableFuture<List<T>> {
    return map { f -> f.toCompletableFuture().recover<T>(defaultValueMapper) }.sequence(executor)
}

/**
 * 복수의 [CompletionStage] 중 가장 처음에 완료되는 것을 반환하고, 나머지는 취소합니다.
 *
 * @param T  [CompletionStage]의 결과 수형
 * @receiver [CompletionStage]의 컬렉션
 * @return 가장 먼저 완료된 [CompletableFuture]
 */
fun <T> Iterable<CompletionStage<T>>.firstCompleted(): CompletableFuture<T> {
    val promise = CompletableFuture<T>()

    return StructuredTaskScope.ShutdownOnSuccess<T>("first-completed", Thread.ofVirtual().factory()).use { scope ->
        this@firstCompleted.forEach { item ->
            scope.fork {
                item.toCompletableFuture().get()
            }
        }

        scope.join()
        try {
            promise.complete(scope.result())
        } catch (e: Throwable) {
            promise.completeExceptionally(e)
        }
        promise
    }
}

/**
 * 결과를 다른 값으로 mapping 하도록 합니다.
 *
 * @receiver CompletionStage<V>
 * @param executor Executor
 * @param mapper Function1<V, R>
 * @return CompletionStage<R>
 */
inline fun <V, R> CompletionStage<V>.map(
    executor: Executor = ForkJoinExecutor,
    crossinline mapper: (V) -> R,
): CompletionStage<R> = thenApplyAsync({ mapper(it) }, executor)

inline fun <V, R> CompletionStage<V>.flatMap(
    executor: Executor = ForkJoinExecutor,
    crossinline mapper: (V) -> CompletionStage<R>,
): CompletionStage<R> = thenComposeAsync({ mapper(it) }, executor)

inline fun <V, R> CompletionStage<V>.mapResult(
    executor: Executor = ForkJoinExecutor,
    crossinline handler: (V?, Throwable?) -> R,
): CompletionStage<R> = handleAsync({ v, e -> handler(v, e) }, executor)

fun <V> CompletionStage<out CompletionStage<V>>.flatten(executor: Executor = ForkJoinExecutor): CompletionStage<V> =
    flatMap(executor) { it }

fun <V> CompletionStage<out CompletionStage<V>>.dereference(executor: Executor = ForkJoinExecutor): CompletionStage<V> =
    flatMap(executor) { it }

fun <V> CompletionStage<V>.wrap(executor: Executor = ForkJoinExecutor): CompletionStage<CompletionStage<V>> =
    map(executor) { CompletableFuture.completedFuture(it) }


/**
 * 두 개의 [CompletionStage]를 순서대로 작업되도록 합니다.
 */
fun <R, A, B> combineOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B) -> R,
): CompletionStage<R> = a.thenCombineAsync(b, BiFunction(combiner), executor)

/**
 * 세 개의 [CompletionStage]를 순서대로 작업되도록 합니다.
 */
@Suppress("UNCHECKED_CAST")
fun <R, A, B, C> combineOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C) -> R,
): CompletionStage<R> {
    return listOf(a, b, c).sequence(executor).map { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C)
        }
}

/**
 * 네 개의 [CompletionStage]를 순서대로 작업되도록 합니다.
 */
@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D> combineOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D) -> R,
): CompletionStage<R> {
    return listOf(a, b, c, d).sequence(executor).map { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D)
        }
}

/**
 * 다섯 개의 [CompletionStage]를 순서대로 작업되도록 합니다.
 */
@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D, E> combineOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    e: CompletionStage<E>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D, E) -> R,
): CompletionStage<R> {
    return listOf(a, b, c, d, e).sequence(executor).map { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D, list[4] as E)
        }
}

/**
 * 여섯 개의 [CompletionStage]를 순서대로 작업되도록 합니다.
 */
@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D, E, F> combineOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    e: CompletionStage<E>,
    f: CompletionStage<F>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D, E, F) -> R,
): CompletionStage<R> {
    return listOf(a, b, c, d, e, f).sequence(executor).map { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D, list[4] as E, list[5] as F)
        }
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B> combineFutureOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B) -> CompletionStage<R>,
): CompletionStage<R> {
    return listOf(a, b).sequence(executor).flatMap { list ->
            combiner(list[0] as A, list[1] as B)
        }
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B, C> combineFutureOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C) -> CompletionStage<R>,
): CompletionStage<R> {
    return listOf(a, b, c).sequence(executor).flatMap { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C)
        }
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D> combineFutureOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D) -> CompletionStage<R>,
): CompletionStage<R> {
    return listOf(a, b, c, d).sequence(executor).flatMap { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D)
        }
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D, E> combineFutureOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    e: CompletionStage<E>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D, E) -> CompletionStage<R>,
): CompletionStage<R> {
    return listOf(a, b, c, d, e).sequence(executor).flatMap { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D, list[4] as E)
        }
}

@Suppress("UNCHECKED_CAST")
fun <R, A, B, C, D, E, F> combineFutureOf(
    a: CompletionStage<A>,
    b: CompletionStage<B>,
    c: CompletionStage<C>,
    d: CompletionStage<D>,
    e: CompletionStage<E>,
    f: CompletionStage<F>,
    executor: Executor = ForkJoinExecutor,
    combiner: (A, B, C, D, E, F) -> CompletionStage<R>,
): CompletionStage<R> {
    return listOf(a, b, c, d, e, f).sequence(executor).flatMap { list ->
            combiner(list[0] as A, list[1] as B, list[2] as C, list[3] as D, list[4] as E, list[5] as F)
        }
}
