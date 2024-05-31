package io.bluetape4k.coroutines.support

import io.bluetape4k.coroutines.context.PropertyCoroutineContext
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext

private val logger by lazy { KotlinLogging.logger {} }

suspend fun logging(msg: String) {
    logging { msg }
}

suspend fun logging(msg: suspend () -> Any?) {
    val name = coroutineContext[CoroutineName]?.name
    val props = coroutineContext[PropertyCoroutineContext]?.properties
    if (props != null) {
        if (name != null) {
            logger.debug { "[$name, $props] ${msg.invoke()}" }
        } else {
            logger.debug { "[$props] ${msg.invoke()}" }
        }
    } else if (name != null) {
        logger.debug { "[$name] ${msg.invoke()}" }
    } else {
        logger.debug { msg.invoke() }
    }
}

@InternalCoroutinesApi
fun <T: Job> T.log(tag: Any): T = apply {
    invokeOnCompletion(onCancelling = true, invokeImmediately = true) {
        // CancellationException 은 무시합니다.
        if (it is CancellationException) {
            logger.debug { "[$tag] Cancelled" }
        } else {
            logger.debug(it) { "[$tag] Completed" }
        }
    }
}
