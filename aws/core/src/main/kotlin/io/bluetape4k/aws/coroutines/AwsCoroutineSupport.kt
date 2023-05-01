package io.bluetape4k.aws.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * AWS 동기 함수 호출을 suspend 함수로 호출하게 한다.
 *
 * @param context CoroutineContext
 * @param method 실행할 메소드
 * @return 메소드 실행 결과
 */
suspend inline fun <RESULT: Any> suspendCommand(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline method: () -> RESULT,
): RESULT = coroutineScope {
    withContext(newCoroutineContext(context)) {
        method()
    }
}

/**
 * AWS 동기 함수 호출을 suspend 함수로 호출하게 한다.
 *
 * @param context CoroutineContext
 * @param request [method] 의 입력 정보
 * @param method 실행할 메소드
 * @return 메소드 실행 결과
 */
suspend inline fun <REQUEST, RESULT: Any> suspendCommand(
    context: CoroutineContext = EmptyCoroutineContext,
    request: REQUEST,
    crossinline method: (request: REQUEST) -> RESULT,
): RESULT = coroutineScope {
    withContext(newCoroutineContext(context)) {
        method(request)
    }
}
