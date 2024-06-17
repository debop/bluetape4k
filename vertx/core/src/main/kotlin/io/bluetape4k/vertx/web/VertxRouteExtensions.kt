package io.bluetape4k.vertx.web

import io.bluetape4k.vertx.currentVertxCoroutineScope
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.launch

/**
 * route 핸들러 목록에 요청 핸들러를 추가합니다.
 *
 * @param requestHandler request handler
 * @return a reference to this, so the API can be used fluently
 */
suspend inline fun Route.coHandler(
    crossinline requestHandler: suspend (RoutingContext) -> Unit,
): Route {
    return handler { ctx ->
        currentVertxCoroutineScope().launch {
            try {
                requestHandler(ctx)
            } catch (e: Throwable) {
                ctx.fail(e)
            }
        }
    }
}

/**
 * 실패 route 핸들러 목록에 실패 핸들러(`requestHandler`) 를 추가합니다.
 *
 * @param requestHandler  실패를 처리하는 요청 핸들러
 * @return a reference to this, so the API can be used fluently
 */
suspend inline fun Route.coFailureHandler(
    crossinline requestHandler: suspend (RoutingContext) -> Unit,
): Route {
    return failureHandler { ctx ->
        currentVertxCoroutineScope().launch {
            try {
                requestHandler(ctx)
            } catch (e: Throwable) {
                ctx.fail(e)
            }
        }
    }
}
