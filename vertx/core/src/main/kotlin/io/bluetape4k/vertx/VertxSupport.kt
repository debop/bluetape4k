package io.bluetape4k.vertx

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

/**
 * Get current [Vertx] instance.
 */
fun currentVertx(): Vertx = Vertx.currentContext()?.owner() ?: Vertx.vertx()

/**
 * Run the block with the [Vertx] dispatcher.
 */
suspend inline fun <T> Vertx.withVertxDispatcher(crossinline block: suspend CoroutineScope.() -> T): T {
    return withContext(this.dispatcher()) {
        block(this)
    }
}
