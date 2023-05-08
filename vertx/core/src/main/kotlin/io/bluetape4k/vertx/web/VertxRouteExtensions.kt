package io.bluetape4k.vertx.web

import io.bluetape4k.vertx.currentVertxCoroutineScope
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.launch

/**
 * Append a request handler to the route handlers list in Coroutines.
 *
 * @param requestHandler request handler
 * @return a reference to this, so the API can be used fluently
 */
suspend inline fun Route.coHandler(
    crossinline requestHandler: suspend (RoutingContext) -> Unit,
): Route {
    return handler { ctx ->
        val scope = currentVertxCoroutineScope()
        scope.launch {
            try {
                requestHandler(ctx)
            } catch (e: Throwable) {
                ctx.fail(e)
            }
        }
    }
}

/**
 * Append a failure handler to the route failure handlers list.
 *
 * @param requestHandler   the request handler
 * @return a reference to this, so the API can be used fluently
 */
suspend inline fun Route.coFailureHandler(
    crossinline requestHandler: suspend (RoutingContext) -> Unit,
): Route {
    return failureHandler { ctx ->
        val scope = currentVertxCoroutineScope()
        scope.launch {
            try {
                requestHandler(ctx)
            } catch (e: Throwable) {
                ctx.fail(e)
            }
        }
    }
}
