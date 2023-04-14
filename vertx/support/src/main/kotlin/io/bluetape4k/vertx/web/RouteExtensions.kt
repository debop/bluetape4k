package io.bluetape4k.vertx.web

import io.bluetape4k.vertx.currentVertxDispatcher
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Append a request handler to the route handlers list in Coroutines.
 *
 * @param requestHandler request handler
 * @return a reference to this, so the API can be used fluently
 */
suspend fun Route.coHandler(
    requestHandler: suspend (RoutingContext) -> Unit,
): Route = coroutineScope {
    handler { ctx ->
        launch(currentVertxDispatcher()) {
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
suspend fun Route.coFailureHandler(
    requestHandler: suspend (RoutingContext) -> Unit,
): Route = coroutineScope {
    failureHandler { ctx ->
        launch(currentVertxDispatcher()) {
            try {
                requestHandler(ctx)
            } catch (e: Throwable) {
                ctx.fail(e)
            }
        }
    }
}
