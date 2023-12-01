package io.bluetape4k.logback.kafka.keycreator

/**
 * 발송할 로그 Event를 기준으로 Key 를 생성합니다.
 *
 * @param E [ILoggingEvent]의 수형
 */
interface KeyCreator<E> {

    /**
     * Key 생성
     *
     * @param e
     * @return
     */
    fun create(e: E): ByteArray?
}
