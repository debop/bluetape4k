package io.bluetape4k.core.concurrency

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

object FutureUtils {

    fun <V> firstCompleted(
        futures: Iterable<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
    ): CompletableFuture<V> {
        val future = CompletableFuture<V>()

        // 모든 CompletableFuture 중 먼저 종료한 것의 값을 `future`에 설정합니다.
        futures.forEach { item ->
            item.onComplete(executor,
                successHandler = { future.complete(it) },
                failureHandler = { future.completeExceptionally(it) })
        }

        return future
    }

    /**
     * `CompletableFuture` 컬렉션을 `CompletableFuture<List>` 로 변환합니다.
     *
     * @receiver Iterable<CompletableFuture<V>>
     * @param executor Executor
     * @return CompletableFuture<List<V>>
     */
    fun <V> allAsList(
        futures: Iterable<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
    ): CompletableFuture<List<V>> {
        return futures
            .fold(completableFutureOf(mutableListOf<V>())) { futureAcc, future ->
                futureAcc.zip(future, executor) { acc, result ->
                    acc.add(result)
                    acc
                }
            }
            .map(executor) { it.toList() }
    }

    /**
     * `CompletableFuture` 컬렉션에서 성공한 결과들만 반환하도록 합니다.
     * @receiver Iterable<CompletableFuture<V>>
     * @param executor Executor
     * @return CompletableFuture<List<V>>
     */
    fun <V> successfulAsList(
        futures: Iterable<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
    ): CompletableFuture<List<V>> {
        return futures
            .fold(completableFutureOf(mutableListOf<V>())) { futureAcc, future ->
                futureAcc.flatMap(executor) { acc ->
                    future.map(executor) {
                        it?.run { acc.add(this) }
                        acc
                    }
                }
            }
            .map(executor) { it.toList() }
    }


    fun <V, R> fold(
        iterator: Iterator<CompletableFuture<V>>,
        initial: R,
        executor: Executor = ForkJoinExecutor,
        op: (R, V) -> R,
    ): CompletableFuture<R> {
        return if (!iterator.hasNext()) completableFutureOf(initial)
        else iterator.next().flatMap(executor) { fold(iterator, op(initial, it), executor, op) }
    }

    fun <V, R> fold(
        futures: Iterable<CompletableFuture<V>>,
        initial: R,
        executor: Executor = ForkJoinExecutor,
        op: (R, V) -> R,
    ): CompletableFuture<R> {
        return fold(futures.iterator(), initial, executor, op)
    }

    fun <V> reduce(
        iterator: Iterator<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
        op: (V, V) -> V,
    ): CompletableFuture<V> {
        return if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
        else iterator.next().flatMap(executor) { fold(iterator, it, executor, op) }
    }

    fun <V> reduce(
        futures: Iterable<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
        op: (V, V) -> V,
    ): CompletableFuture<V> {
        return reduce(futures.iterator(), executor, op)
    }

    fun <V, R> transform(
        futures: Iterable<CompletableFuture<V>>,
        executor: Executor = ForkJoinExecutor,
        action: (V) -> R,
    ): CompletableFuture<List<R>> {
        return futures.fold(completableFutureOf(mutableListOf<R>())) { futureAcc, future ->
            futureAcc.zip(future, executor) { acc, result ->
                acc.add(action(result))
                acc
            }
        }.map(executor) { it.toList() }
    }

    /**
     * `futures`의 모든 결과를 `combiner`로 하나의 결과로 결합합니다.
     *
     * @param futures Iterable<CompletableFuture<out Any?>>
     * @param executor Executor
     * @param combiner Function1<List<Any?>, R>
     * @return CompletableFuture<R>
     */
    fun <R> combine(
        futures: Iterable<CompletableFuture<out Any?>>,
        executor: Executor = ForkJoinExecutor,
        combiner: (List<Any?>) -> R,
    ): CompletableFuture<R> {
        return futures
            .fold(completableFutureOf(mutableListOf<Any?>())) { futureAcc, future ->
                futureAcc.zip(future, executor) { acc, result ->
                    acc.add(result)
                    acc
                }
            }
            .map(executor) { it.toList() }
            .thenApplyAsync({ result -> combiner(result) }, executor)
    }
}
