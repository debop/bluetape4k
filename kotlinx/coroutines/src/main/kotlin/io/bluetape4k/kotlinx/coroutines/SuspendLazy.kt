package io.bluetape4k.kotlinx.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * lazy operation의 실행 블럭에 suspend 함수를 사용하도록 하는 interface
 *
 * @param T 지연된 계산을 수행한 결과 값의 수형
 */
fun interface SuspendLazy<out T> {
    suspend operator fun invoke(): T
}

private class SuspendLazyBlockImpl<out T>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    initializer: () -> T,
): SuspendLazy<T> {

    private val lazyValue: Lazy<T> = lazy(initializer)

    override suspend fun invoke(): T = with(lazyValue) {
        if (isInitialized()) value else withContext(dispatcher) { value }
    }
}

private class SuspendLazySuspendingImpl<out T>(
    coroutineScope: CoroutineScope,
    coroutineContext: CoroutineContext,
    initializer: suspend CoroutineScope.() -> T,
): SuspendLazy<T> {

    private val deferredValue by lazy {
        coroutineScope.async(coroutineContext, start = CoroutineStart.LAZY, block = initializer)
    }

    override suspend fun invoke(): T = deferredValue.await()
}

/**
 * [Lazy]와 같이 값 계산을 지연해서 수형하는데, 값 계산을 Coroutines 환경 하에서 수행하게 된다.
 * 값을 조회할 때, Coroutine Scope 에서 수행해야 한다.
 *
 * ```
 * val lazyValue: SuspendLazy<Int> = suspendBlockingLazy {
 *      Thread.sleep(100)
 *      42
 * }
 *
 * val value = runBlocking { lazyValue() }
 * ```
 *
 * @param T
 * @param dispatcher
 * @param initializer
 * @return
 */
fun <T> suspendBlockingLazy(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    initializer: () -> T,
): SuspendLazy<T> =
    SuspendLazyBlockImpl(dispatcher, initializer)

/**
 * [Lazy]와 같이 값 계산을 지연해서 수형하는데, 값 계산을 [Dispatchers.IO] 환경 하에서 함수에서 수행하게 된다.
 * 값을 조회할 때, Coroutine Scope 에서 수행해야 한다.
 *
 * ```
 * val lazyValue: SuspendLazy<Int> = suspendBlockingLazyIO {
 *      Thread.sleep(100)
 *      42
 * }
 *
 * val value = runBlocking { lazyValue() }
 * ```
 *
 * @param T
 * @param initializer
 * @return
 */
fun <T> suspendBlockingLazyIO(initializer: () -> T): SuspendLazy<T> =
    SuspendLazyBlockImpl(Dispatchers.IO, initializer)

/**
 * 지연된 값을 구할 때 suspend 함수를 이용하여 비동기 방식으로 구하고, 값을 조회할 때도 CoroutineScope 하에서 구합니다.
 *
 * ```
 * runBlocking {
 *      val lazyValue: SuspendLazy<T> = suspendLazy {
 *          delay(100)
 *          42
 *      }
 *      val value = lazyValue()
 * }
 * ```
 *
 * @param T
 * @param context
 * @param initializer
 * @return
 */
fun <T> CoroutineScope.suspendLazy(
    context: CoroutineContext = EmptyCoroutineContext,
    initializer: suspend CoroutineScope.() -> T,
): SuspendLazy<T> =
    SuspendLazySuspendingImpl(this, context, initializer)
