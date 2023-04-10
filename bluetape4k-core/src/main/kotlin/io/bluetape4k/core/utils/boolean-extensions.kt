package io.bluetape4k.core.utils

/**
 * 값이 true 라면 [block]을 실행합니다. false라면 null을 반환합니다.
 */
inline fun <T : Any> Boolean.ifTrue(block: () -> T): T? = when (this) {
    true -> block()
    else -> null
}

/**
 * 함수가 true를 반환하면 [block]을 실행합니다. 아니면 null을 반환합니다.
 */
inline fun <T : Any> (() -> Boolean).ifTrue(block: () -> T): T? = this().ifTrue(block)

/**
 * 값이 true 라면 [block]을 실행합니다. false라면 null을 반환합니다.
 */
inline fun <T : Any> Boolean.ifFalse(block: () -> T): T? = when (this) {
    false -> block()
    else -> null
}

/**
 * 함수가 false를 반환하면 [block]을 실행합니다. 아니면 null을 반환합니다.
 */
inline fun <T : Any> (() -> Boolean).ifFalse(block: () -> T): T? = this().ifFalse(block)
