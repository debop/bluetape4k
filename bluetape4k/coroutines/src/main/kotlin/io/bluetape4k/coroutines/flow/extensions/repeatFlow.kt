package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.core.assertPositiveNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * [times]만큼 반복해서 [action]을 수행하며, 결과를 flow 로 emit 합니다.
 *
 * @param T
 * @param times 반복 횟수 (0보다 커야 함)
 * @param action 반복 실행할 function
 * @return
 */
inline fun <T> repeatFlow(
    times: Int,
    crossinline action: suspend (index: Int) -> T,
): Flow<T> = flow {
    times.assertPositiveNumber("times")

    repeat(times) { index ->
        emit(action(index))
    }
}
