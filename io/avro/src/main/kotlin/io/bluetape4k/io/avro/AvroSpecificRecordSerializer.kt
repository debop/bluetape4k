package io.bluetape4k.io.avro

import io.bluetape4k.codec.decodeBase64ByteArray
import io.bluetape4k.codec.encodeBase64String
import org.apache.avro.specific.SpecificRecord

/**
 * Avro [SpecificRecord] 인스턴스를 직렬화/역직렬화를 수행합니다.
 */
interface AvroSpecificRecordSerializer {

    /**
     * Avro [SpecificRecord] 인스턴스를 직렬화하여 [ByteArray]로 반환합니다.
     *
     * @param graph 직렬화할 Avro [SpecificRecord] 객체
     * @return 직렬화된 데이터, 실패 시에는 null을 반환
     */
    fun <T: SpecificRecord> serialize(graph: T?): ByteArray?

    /**
     * Avro [SpecificRecord]의 직렬화된 정보를 역직렬화하여 [class] 형식의 인스턴스를 빌드합니다.
     *
     * @param avroBytes [SpecificRecord]의 직렬화된 정보
     * @param clazz 대상 수형 정보
     * @return 역직렬화된 인스턴스, 실패 시에는 null 반환
     */
    fun <T: SpecificRecord> deserialize(avroBytes: ByteArray?, clazz: Class<T>): T?


    /**
     * Avro [SpecificRecord]를 직렬화하여 Base64 문자열로 반환합니다.
     *
     * @param T 대상 수형
     * @param graph 대상 Avro 인스턴스
     * @return Base64 인코딩된 문자열
     */
    fun <T: SpecificRecord> serializeAsString(graph: T?): String? {
        return graph?.run { serialize(this)?.encodeBase64String() }
    }

    /**
     * Avro [SpecificRecord]의 직렬화된 정보를 역직렬화하여 [class] 형식의 인스턴스를 빌드합니다.
     *
     * @param T 대상 수형
     * @param avroBytes [SpecificRecord]의 직렬화된 정보
     * @return 역직렬화된 Avro 인스턴스, 실패 시에는 null 반환
     */
    fun <T: SpecificRecord> deserializeFromString(avroText: String?, clazz: Class<T>): T? {
        return avroText?.run { deserialize(this.decodeBase64ByteArray(), clazz) }
    }
}

/**
 * [base64String]을 읽어 Avro 역직렬화를 수행하여, 지정된 수형의 인스턴스를 빌드합니다.
 *
 * @param T 역직렬화할 수형
 * @param base64String Avro 직렬화된 문자열
 * @param clazz 역직렬화할 수형
 * @return 역직렬화된 Avro 인스턴스, 실패 시에는 null 반환
 */
inline fun <reified T: SpecificRecord> AvroSpecificRecordSerializer.deserialize(avroBytes: ByteArray?): T? {
    return deserialize(avroBytes, T::class.java)
}

/**
 * [base64String]을 읽어 Avro 역직렬화를 수행하여, 지정된 수형의 인스턴스를 빌드합니다.
 *
 * @param T 역직렬화할 수형
 * @param base64String Avro 직렬화된 문자열
 * @return 역직렬화된 Avro 인스턴스, 실패 시에는 null 반환
 */
inline fun <reified T: SpecificRecord> AvroSpecificRecordSerializer.deserializeFromString(avroText: String?): T? {
    return deserializeFromString(avroText, T::class.java)
}
