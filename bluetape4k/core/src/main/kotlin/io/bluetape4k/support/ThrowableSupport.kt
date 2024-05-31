package io.bluetape4k.support

/**
 * Build a message for the given base message and root cause.
 */
fun Throwable?.buildMessage(message: String?): String? {
    if (this == null) {
        return message
    }
    return buildString {
        message?.run { append(this).append("; ") }
        append("nested exception is ").append(cause)
    }
}

/**
 * Retrieve the innermost cause of the given exception, if any.
 *
 * @return the innermost exception, or `null` if none
 */
fun Throwable.getRootCause(): Throwable? {
    var rootCause: Throwable? = null
    var cause = this.cause
    while (cause != null && cause != rootCause) {
        rootCause = cause
        cause = cause.cause
    }
    return rootCause
}

/**
 * 주어진 예외의 가장 구체적인 원인, 즉 가장 내부적인 원인(근본 원인) 또는 예외 자체를 검색합니다.
 *
 * 근본 원인이 없는 경우 원래 예외로 되돌아간다는 점에서 [getRootCause]와 다릅니다.
 *
 * @return the most specific cause (never `null`)
 */
fun Throwable.getMostSpecificCause(): Throwable = getRootCause() ?: this
