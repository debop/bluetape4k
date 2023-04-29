package io.bluetape4k.infra.otel.coroutines

import io.bluetape4k.infra.otel.currentOtelContext
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T> withOtelContext(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    otelContext: Context = currentOtelContext(),
    block: suspend CoroutineScope.() -> T,
): T {
    val coContext = when {
        coroutineContext != EmptyCoroutineContext -> coroutineContext
        else                                      -> currentCoroutineContext()
    }

    return withContext(coContext + otelContext.asContextElement()) {
        block(this)
    }
}
