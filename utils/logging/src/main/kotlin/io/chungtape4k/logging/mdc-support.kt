package io.chungtape4k.logging

import org.slf4j.MDC

/**
 * MDC를 사용하여 로깅 컨텍스트를 설정합니다.
 *
 * ```
 * withLoggingContext("traceId" to traceId) {
 *     log.debug { "Some messages ..." }
 * }
 *
 * withLoggingContext("userId" to userId, preservePrevious = false) {
 *     log.debug { "Some messages ..." }
 * }
 * ```
 *
 * @param pair 로깅 컨텍스트에 추가할 키-값 쌍
 * @param restorePrevious 이전에 설정된 로깅 컨텍스트를 유지할지 여부
 * @param block 로깅 컨텍스트가 설정된 블록
 * @return 블록의 실행 결과
 */
inline fun <T> withLoggingContext(
    pair: Pair<String, Any?>,
    restorePrevious: Boolean = true,
    block: () -> T
): T {
    return if (pair.second == null) {
        block()
    } else if (!restorePrevious) {
        MDC.putCloseable(pair.first, pair.second.toString()).use { block() }
    } else {
        val previousValue = MDC.get(pair.first)
        try {
            MDC.putCloseable(pair.first, pair.second.toString()).use { block() }
        } finally {
            previousValue?.run { MDC.put(pair.first, this) }
        }
    }
}

/**
 * MDC를 사용하여 로깅 컨텍스트를 설정합니다.
 *
 * ```
 * withLoggingContext("key1" to "value1", "key2" to "value2") {
 *     log.debug { "Some messages ..." }
 * }
 * ```
 *
 * @param map 로깅 컨텍스트에 추가할 키-값 쌍
 * @param restorePrevious 이전에 설정된 로깅 컨텍스트를 유지할지 여부
 * @param block 로깅 컨텍스트가 설정된 블록
 * @return 블록의 실행 결과
 */
inline fun <T> withLoggingContext(
    vararg pairs: Pair<String, Any?>,
    restorePrevious: Boolean = true,
    block: () -> T
): T {
    return withLoggingContext(pairs.filter { it.second != null }.toMap(), restorePrevious, block)
}

/**
 * MDC를 사용하여 로깅 컨텍스트를 설정합니다.
 *
 * ```
 * withLoggingContext(mapOf("key1" to "value1", "key2" to "value2")) {
 *     log.debug { "Some messages ..." }
 * }
 * ```
 *
 * @param map 로깅 컨텍스트에 추가할 키-값 쌍
 * @param restorePrevious 이전에 설정된 로깅 컨텍스트를 유지할지 여부
 * @param block 로깅 컨텍스트가 설정된 블록
 * @return 블록의 실행 결과
 */
inline fun <T> withLoggingContext(
    map: Map<String, Any?>,
    restorePrevious: Boolean = true,
    block: () -> T
): T {
    val mdcMap = map.filter { it.value != null }
    val cleanupCallback: List<() -> Unit> = mdcMap.keys.map { key ->
        val previousValue = MDC.get(key)
        if (previousValue != null && restorePrevious) {
            { MDC.put(key, previousValue) }
        } else {
            { MDC.remove(key) }
        }
    }

    return try {
        mdcMap.forEach { (key, value) ->
            MDC.put(key, value.toString())
        }
        block()
    } finally {
        cleanupCallback.forEach { it.invoke() }
    }
}
