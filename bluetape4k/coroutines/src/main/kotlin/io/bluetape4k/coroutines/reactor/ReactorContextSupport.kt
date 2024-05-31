package io.bluetape4k.coroutines.reactor

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactor.ReactorContext
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext

/**
 * 현 Coroutine Context에서 Reactor용 [Context] 를 가져옵니다. 없다면 null 반환
 *
 * ```
 * var captured: String? = null
 *
 * val flow = flow {
 *     // captured = currentCoroutineContext()[ReactorContext]?.context?.getOrNull(key)
 *     captured = currentReactiveContext()?.getOrNull(key)
 *     emit("A")
 * }
 *
 * flow.asFlux()
 *     .contextWrite { ctx -> ctx.put(key, value) }
 *     .subscribe()
 *
 * captured shouldBeEqualTo value
 * ```
 * 참고: [ReactorContext](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactor/kotlinx.coroutines.reactor/-reactor-context/)
 */
suspend inline fun currentReactiveContext(): Context? =
    currentCoroutineContext()[ReactorContext]?.context

fun CoroutineContext.getReactiveContext(): Context? =
    this[ReactorContext]?.context

fun <T: Any> Context.getOrNull(key: Any): T? {
    return if (hasKey(key)) get(key) else null
}

/**
 * 현 Coroutine Context에서 Reactor용 [Context]의 [key]에 해당하는 값을 가져옵니다. 없다면 null 반환
 */
suspend inline fun <T: Any> getReactorContextValueOrNull(key: Any): T? =
    currentReactiveContext()?.getOrNull(key)

/**
 * Reactor [Context]에 저장된 정보를 Coroutines 환경 하에서 사용하기 위한 확장 함수입니다.
 */
fun <T: Any> CoroutineContext.getReactorContextValueOrNull(key: Any): T? {
    return getReactiveContext()?.getOrNull(key)
}
