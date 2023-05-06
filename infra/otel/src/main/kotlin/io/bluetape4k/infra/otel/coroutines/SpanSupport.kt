package io.bluetape4k.infra.otel.coroutines

import io.bluetape4k.infra.otel.trace.use
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 새로운 Span 을 생성하여, [block]을 Coroutines 환경 하에서 실행합니다.
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param coroutineContext [block]을 실행할 때 사용할 CoroutineContext 입니다. 기본 값은 현재 Context 입니다.
 * @param block CoroutineScope 하에서 실행할 코드 블록입니다.
 * @return [block]의 실행 결과입니다.
 */
suspend inline fun <T> SpanBuilder.useSpanAwait(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend (Span) -> T,
): T {
    return startSpan().use { span ->
        withSpanContext(span, coroutineContext) {
            block(it)
        }
    }
}

/**
 * 새로운 Span 을 생성하여, [block]을 Coroutines 환경 하에서 실행합니다.
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param waitTimeout span을 종료할 때까지 대기할 시간입니다. 기본 값은 null 입니다.
 * @param coroutineContext [block]을 실행할 때 사용할 CoroutineContext 입니다. 기본 값은 현재 Context 입니다.
 * @param block CoroutineScope 하에서 실행할 코드 블록입니다.
 * @return [block]의 실행 결과입니다.
 */
suspend inline fun <T> SpanBuilder.useSpanAwait(
    waitTimeout: Long? = null,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend (Span) -> T,
): T {
    return startSpan().use(waitTimeout) { span ->
        withSpanContext(span, coroutineContext) {
            block(it)
        }
    }
}

/**
 * 새로운 Span 을 생성하여, [block]을 Coroutines 환경 하에서 실행합니다.
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param waitDuration span을 종료할 때까지 대기할 시간입니다.
 * @param coroutineContext [block]을 실행할 때 사용할 CoroutineContext 입니다. 기본 값은 현재 Context 입니다.
 * @param block CoroutineScope 하에서 실행할 코드 블록입니다.
 * @return [block]의 실행 결과입니다.
 */
suspend inline fun <T> SpanBuilder.useSpanAwait(
    waitDuration: Duration,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend (Span) -> T,
): T {
    return useSpanAwait(waitDuration.toMillis().coerceAtLeast(0L), coroutineContext, block)
}

/**
 * [span]이 속한 Context 하에서 [block]을 실행합니다.
 * Coroutines 환경 하에서는 OpenTelemetry의 Context가 전달 안되기 때문에 `withContext`에 `span.asContextElement()`를 전달합니다.
 *
 * @see [withOtelContext]
 *
 * @param T [block]의 실행 결과 타입입니다.
 * @param span [block]을 실행할 때 사용할 Span 입니다.
 * @param block [span]이 속한 Context 하에서 실행할 Coroutines 코드 블록입니다.
 * @receiver CoroutineScope
 * @return [block]의 실행 결과입니다.
 */
suspend fun <T> withSpanContext(
    span: Span,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend (Span) -> T,
): T {
    val coContext = when {
        coroutineContext != EmptyCoroutineContext -> coroutineContext
        else                                      -> currentCoroutineContext()
    }

    return withContext(coContext + span.asContextElement()) {
        span.makeCurrent().use { block(span) }
    }
}
