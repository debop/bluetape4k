package io.kommons.logging

import org.slf4j.Logger


inline fun logMessageSafe(msg: () -> Any?, fallbackMessage: String = "로그 생성에 실패했습니다"): String {
    return try {
        msg().toString()
    } catch (e: Exception) {
        "$fallbackMessage: $e"
    }
}

inline fun Logger.trace(msg: () -> Any?) {
    if (isTraceEnabled) {
        trace(logMessageSafe(msg))
    }
}
