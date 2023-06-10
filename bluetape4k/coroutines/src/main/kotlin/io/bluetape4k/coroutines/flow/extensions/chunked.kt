package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Flow 를 [size] 만큼씩 조각내어 `Flow<List<T>>` 로 변한합니다.
 *
 * ```
 * val flow = flowOf(1,2,3,4,5)
 * val chunked = flow.chunked(3)   // {1,2,3}, {4,5}
 * ```

 *
 * @param size chunk size. (require greater than 0)
 */
fun <T> Flow<T>.chunked(size: Int): Flow<List<T>> = windowed(size, size)
