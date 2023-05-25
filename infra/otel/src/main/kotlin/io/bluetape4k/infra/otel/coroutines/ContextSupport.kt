package io.bluetape4k.infra.otel.coroutines

import io.bluetape4k.infra.otel.currentOtelContext
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Current Coroutine Context와 Opentelemetry [Context] 하에서 [block]을 실행합니다.
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param block 실행할 코드 블록입니다.
 * @return [block]의 실행 결과입니다.
 */
suspend inline fun <T> withOtelContext(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    otelContext: Context = currentOtelContext(),
    crossinline block: suspend CoroutineScope.() -> T,
): T {
    val coContext = when {
        coroutineContext != EmptyCoroutineContext -> coroutineContext
        else                                      -> currentCoroutineContext()
    }

    return withContext(coContext + otelContext.asContextElement()) {
        block()
    }
}

/**
 * Current Coroutine Context와 Current Opentelemetry [Context] 하에서 [block]을 실행합니다.
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param block 실행할 코드 블록입니다.
 * @return [block]의 실행 결과입니다.
 */
suspend inline fun <T> Context.withOtelCurrent(
    crossinline block: suspend CoroutineScope.() -> T,
): T {
    return withContext(coroutineContext + this.asContextElement()) {
        block()
    }
}
