package io.bluetape4k.io.json

import io.bluetape4k.support.EMPTY_STRING
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String

interface JsonSerializer {

    /**
     * 객체 상태를 JSON으로 직렬화합니다.
     *
     * @param graph 직렬화할 객체
     * @return JSON 직렬화 결과
     */
    fun serialize(graph: Any?): ByteArray

    /**
     * JSON으로 직렬화된 [ByteArray]를 읽어, 객체로 변환합니다.
     *
     * @param bytes JSON 직렬화된 데이터
     * @param clazz 역직렬화할 대상 수형
     * @return 역직렬화된 객체, 실패시 null 반환
     */
    fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T?

    fun serializeAsString(graph: Any?): String =
        graph?.let { serialize(it).toUtf8String() } ?: EMPTY_STRING

    fun <T: Any> deserializeAsString(jsonText: String?, clazz: Class<T>): T? =
        jsonText?.let { deserialize(it.toUtf8Bytes(), clazz) }
}

inline fun <reified T: Any> JsonSerializer.deserialize(bytes: ByteArray?): T? =
    deserialize(bytes, T::class.java)

inline fun <reified T: Any> JsonSerializer.deserializeAsString(jsonText: String?): T? =
    deserializeAsString(jsonText, T::class.java)
