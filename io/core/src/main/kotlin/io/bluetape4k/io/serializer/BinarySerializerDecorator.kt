package io.bluetape4k.io.serializer

import io.bluetape4k.core.support.emptyByteArray
import io.bluetape4k.io.compressor.Compressor


/**
 * [BinarySerializer]를 Decorator pattern으로 사용하기 위한 클래스
 */
abstract class BinarySerializerDecorator(serializer: BinarySerializer) : BinarySerializer by serializer

/**
 * 압축을 수행하는 [BinarySerializer]
 */
open class CompressableBinarySerializer(
    serializer: BinarySerializer,
    val compressor: Compressor,
) : BinarySerializerDecorator(serializer) {

    override fun serialize(graph: Any?): ByteArray =
        graph?.run { compressor.compress(super.serialize(this)) } ?: emptyByteArray

    override fun <T : Any> deserialize(bytes: ByteArray?): T? =
        bytes?.run { super.deserialize(compressor.decompress(this)) }
}
