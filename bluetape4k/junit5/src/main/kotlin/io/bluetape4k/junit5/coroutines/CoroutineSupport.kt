package io.bluetape4k.junit5.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 테스트 Block을 Blocking 해야 하는데 매번 반환 수형을 지정하는 것이 번거롭다.
 * 이 함수를 사용하면 테스트 코드의 반환값에 상관없이 사용할 수 있다.
 *
 * @param context [CoroutineContext] 인스턴스 (기본: [Dispatchers.Default])
 * @param testBody  테스트 할 코드
 */
inline fun runSuspendTest(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline testBody: suspend CoroutineScope.() -> Unit,
) {
    runBlocking(context) {
        testBody.invoke(this)
    }
}

/**
 * 테스트 Block을 Blocking 해야 하는데 매번 반환 수형을 지정하는 것이 번거롭다.
 * 이 함수를 사용하면 테스트 코드의 반환값에 상관없이 사용할 수 있다.
 *
 * @param testBody  테스트 할 코드
 */
inline fun runSuspendWithIO(crossinline testBody: suspend CoroutineScope.() -> Unit) {
    runBlocking(Dispatchers.IO) {
        testBody.invoke(this)
    }
}
