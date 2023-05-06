package io.bluetape4k.examples.coroutines

import io.bluetape4k.core.requirePositiveNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun Int.isEven(): Boolean = this % 2 == 0

fun Int.isOdd(): Boolean = this % 2 == 1

/**
 * 테스트 시 과부하를 주기 위해 [action]을 [times]*[times]만큼 반복적으로 수행합니다.
 *
 * @param coroutineContext
 * @param times
 * @param action
 * @receiver
 */
suspend fun massiveRun(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    times: Int = 1000,
    action: suspend () -> Unit,
) {
    times.requirePositiveNumber("times")
    val ctx = when {
        coroutineContext != EmptyCoroutineContext -> coroutineContext
        else                                      -> currentCoroutineContext()
    }

    // withContext 이므로 내부의 Job을 완료한 후 반환합니다
    withContext(ctx) {
        repeat(times) {
            launch {
                repeat(times) {
                    action()
                }
            }
        }
    }
}
