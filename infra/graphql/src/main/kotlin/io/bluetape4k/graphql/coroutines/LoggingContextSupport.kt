package io.bluetape4k.graphql.coroutines

import com.netflix.graphql.dgs.context.DgsContext
import graphql.schema.DataFetchingEnvironment
import io.bluetape4k.coroutines.context.getOrCurrent
import io.bluetape4k.graphql.getCopyOfLoggingContextMapOrEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.dataloader.BatchLoaderEnvironment
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T: io.bluetape4k.graphql.LoggingContextProvider> BatchLoaderEnvironment.getLoggingContext(): MDCContext {
    val dgsContext = DgsContext.getCustomContext<T>(this)
    return MDCContext(dgsContext.contextMapOrEmpty + getCopyOfLoggingContextMapOrEmpty())
}

fun <T: io.bluetape4k.graphql.LoggingContextProvider> DataFetchingEnvironment.getLoggingContext(): MDCContext {
    val dgsContext = DgsContext.getCustomContext<T>(this)
    return MDCContext(dgsContext.contextMapOrEmpty + getCopyOfLoggingContextMapOrEmpty())
}

suspend fun <T: io.bluetape4k.graphql.LoggingContextProvider> BatchLoaderEnvironment.withLoggingContext(
    block: suspend CoroutineScope.() -> T,
): T = coroutineScope {
    val context = coroutineContext.getOrCurrent() + getLoggingContext<T>()
    withContext(context) {
        block()
    }
}

suspend fun <T: io.bluetape4k.graphql.LoggingContextProvider> DataFetchingEnvironment.withLoggingContext(
    block: suspend CoroutineScope.() -> T,
): T = coroutineScope {
    val context = coroutineContext.getOrCurrent() + getLoggingContext<T>()
    withContext(context) {
        block()
    }
}

suspend fun <T> withGraphqlLoggingContext(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    loggingContext: io.bluetape4k.graphql.LoggingContextProvider,
    block: suspend CoroutineScope.() -> T,
): T = coroutineScope {
    val mdcContext = MDCContext(loggingContext.contextMapOrEmpty + getCopyOfLoggingContextMapOrEmpty())
    withContext(coroutineContext.getOrCurrent() + mdcContext) {
        block()
    }
}
