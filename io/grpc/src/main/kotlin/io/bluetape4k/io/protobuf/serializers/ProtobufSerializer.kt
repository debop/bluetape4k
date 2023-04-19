package io.bluetape4k.io.protobuf.serializers

import io.bluetape4k.io.protobuf.ProtoAny
import io.bluetape4k.io.protobuf.ProtoMessage
import io.bluetape4k.io.serializer.AbstractBinarySerializer
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.io.serializer.BinarySerializers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.isNullOrEmpty
import java.util.concurrent.ConcurrentHashMap

/**
 * Protobuf Message 를 ByteArray 로 직렬화/역직렬화하는 Serializer
 *
 * @property fallbackSerializer 예외시 수행할 fallback serializer
 */
class ProtobufSerializer(
    private val fallbackSerializer: BinarySerializer = BinarySerializers.Jdk,
): AbstractBinarySerializer() {

    companion object: KLogging() {
        private val messageTypes = ConcurrentHashMap<String, Class<out ProtoMessage>>()
    }

    override fun doSerialize(graph: Any): ByteArray {
        return if (graph is ProtoMessage) ProtoAny.pack(graph).toByteArray()
        else fallbackSerializer.serialize(graph)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isNullOrEmpty()) {
            return null
        }
        return try {
            val protoAny = ProtoAny.parseFrom(bytes)
            val className = protoAny.typeUrl.substringAfterLast("/")
            val clazz = messageTypes.getOrPut(className) {
                Class.forName(className) as Class<ProtoMessage>
            }
            protoAny.unpack(clazz) as? T
        } catch (e: Exception) {
            fallbackSerializer.deserialize(bytes)
        }
    }
}
