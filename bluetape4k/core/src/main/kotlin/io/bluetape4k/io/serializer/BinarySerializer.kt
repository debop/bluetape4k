package io.bluetape4k.io.serializer

import io.bluetape4k.io.getBytes
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.isNullOrEmpty
import java.nio.ByteBuffer

/**
 * 객체를 [ByteArray]로 직렬화/역직렬화하는 Serializer
 */
interface BinarySerializer {

    /**
     * 객체를 Binary 방식으로 직렬화합니다.
     *
     * @param graph 직렬화할 객체
     * @return 직렬화된 데이터
     */
    fun serialize(graph: Any?): ByteArray

    /**
     * 직렬화된 데이터를 읽어 대상 객체로 역직렬화합니다.
     *
     * @param T     역직렬화할 객체 수형
     * @param bytes 직렬화된 데이터
     * @return 역직렬화한 객체
     */
    fun <T: Any> deserialize(bytes: ByteArray?): T?

    /**
     * 객체를 Binary 방식으로 직렬화를 하여 [ByteBuffer]로 반환합니다.
     *
     * @param graph 직렬화할 객체
     * @return 직렬화된 정보를 담은 [ByteBuffer] 인스턴스
     */
    fun serializeAsByteBuffer(graph: Any?): ByteBuffer =
        ByteBuffer.wrap(serialize(graph))

    /**
     * 직렬화된 [buffer]를 읽어 대상 객체로 역직렬화합니다.
     *
     * @param T     역직렬화할 객체 수형
     * @param buffer 직렬화된 데이터
     * @return 역직렬화한 객체
     */
    fun <T: Any> deserialize(buffer: ByteBuffer): T? =
        deserialize(buffer.getBytes())
}

/**
 * [BinarySerializer]의 추상화 클래스
 */
abstract class AbstractBinarySerializer: BinarySerializer {

    companion object: KLogging()

    protected abstract fun doSerialize(graph: Any): ByteArray
    protected abstract fun <T: Any> doDeserialize(bytes: ByteArray): T?

    /**
     * 객체를 Binary 방식으로 직렬화합니다.
     *
     * @param graph 직렬화할 객체
     * @return 직렬화된 데이터
     */
    override fun serialize(graph: Any?): ByteArray {
        if (graph == null) {
            emptyByteArray
        }
        return runCatching { doSerialize(graph!!) }
            .onFailure { log.error(it) { "Fail to serialize." } }
            .getOrDefault(emptyByteArray)
    }

    /**
     * 직렬화된 데이터를 읽어 대상 객체로 역직렬화합니다.
     *
     * @param T     역직렬화할 객체 수형
     * @param bytes 직렬화된 데이터
     * @return 역직렬화한 객체
     */
    override fun <T: Any> deserialize(bytes: ByteArray?): T? {
        if (bytes.isNullOrEmpty()) {
            return null
        }
        return runCatching { doDeserialize<T>(bytes!!) }
            .onFailure { log.error(it) { "Fail to deserialize." } }
            .getOrNull()
    }
}
