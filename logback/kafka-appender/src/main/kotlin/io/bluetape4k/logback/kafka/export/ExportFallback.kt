package io.bluetape4k.logback.kafka.export

/**
 * Export 시에 예외가 발생했을 경우, Fallback 처리를 위한 인터페이스
 *
 * @param E Event Type
 */
fun interface ExportFallback<E> {

    fun onFail(event: E, throwable: Throwable?)
}
