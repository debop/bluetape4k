package io.bluetape4k.concurrent.virtualthread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Virtual Thread 를 이용하여 Coroutine 작업을 Blocking 방식으로 수행합니다.
 *
 * @param T
 * @param context
 * @param block
 * @receiver
 * @return
 */
fun <T> runVirtualBlocking(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): T {
    return Executors.newVirtualThreadPerTaskExecutor().use { executor ->
        runBlocking(context + executor.asCoroutineDispatcher(), block)
    }
}

/**
 * Virtual Thread 를 이용하여 Coroutine 작업을 수행합니다.
 *
 * @param T
 * @param context
 * @param block
 * @receiver
 * @return
 */
suspend fun <T> withVirtualContext(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): T {
    return Executors.newVirtualThreadPerTaskExecutor().use { executor ->
        withContext(context + executor.asCoroutineDispatcher(), block)
    }
}
