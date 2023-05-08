package io.bluetape4k.vertx

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Get current [Vertx] instance.
 */
fun currentVertx(): Vertx = Vertx.currentContext()?.owner() ?: Vertx.vertx()

suspend inline fun <T> Vertx.withVertxDispatcher(crossinline block: suspend CoroutineScope.() -> T): T {
    return withContext(this.dispatcher() as CoroutineContext) {
        block(this)
    }
}
