package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import okio.Buffer

/**
 * Kryo 라이브리러를 사용하는 Serializer
 */
class KryoSerializer(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractBinarySerializer() {

    companion object: KLogging()

    override fun doSerialize(graph: Any): ByteArray {
        val buffer = Buffer()

        withKryoOutput { output ->
            output.outputStream = buffer.outputStream()
            withKryo {
                writeClassAndObject(output, graph)
            }
            output.flush()
        }
        return buffer.readByteArray()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isEmpty()) {
            return null
        }

        val buffer = Buffer().write(bytes)
        withKryoInput { input ->
            input.inputStream = buffer.inputStream()
            withKryo {
                return readClassAndObject(input) as? T
            }
        }
    }
}
