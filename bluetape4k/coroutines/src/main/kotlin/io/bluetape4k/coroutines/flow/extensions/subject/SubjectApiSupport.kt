package io.bluetape4k.coroutines.flow.extensions.subject

import kotlinx.coroutines.delay

/**
 * collector가 등록될 때까지 대기합니다.
 */
suspend fun <T> SubjectApi<T>.awaitCollector() {
    while (!hasCollectors) {
        delay(1)
    }
}

/**
 * [minCollectorCount] 갯수만큼 collectors가 등록될 때까지 대기합니다.
 */
suspend fun <T> SubjectApi<T>.awaitCollectors(minCollectorCount: Int) {
    val limit = minCollectorCount.coerceAtLeast(1)
    while (collectorCount < limit) {
        delay(1)
    }
}

fun <L, R> areEqualAsAny(left: L, right: R): Boolean =
    (left as Any == right as Any)
