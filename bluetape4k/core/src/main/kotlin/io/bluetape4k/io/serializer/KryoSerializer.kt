package io.bluetape4k.io.serializer

import io.bluetape4k.io.ApacheByteArrayOutputStream
import io.bluetape4k.logging.KLogging
import java.io.ByteArrayInputStream

/**
 * Kryo 라이브리러를 사용하는 Serializer
 */
class KryoSerializer(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractBinarySerializer() {

    companion object: KLogging()

    override fun doSerialize(graph: Any): ByteArray {
        return ApacheByteArrayOutputStream(bufferSize).use { bos ->
            withKryoOutput { output ->
                output.outputStream = bos
                withKryo {
                    writeClassAndObject(output, graph)
                }
                output.flush()
                bos.toByteArray()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isEmpty()) {
            return null
        }

        return withKryoInput { input ->
            input.inputStream = ByteArrayInputStream(bytes).buffered(bufferSize)
            withKryo {
                readClassAndObject(input) as? T
            }
        }
    }
}
