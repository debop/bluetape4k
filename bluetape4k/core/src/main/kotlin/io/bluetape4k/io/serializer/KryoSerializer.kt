package io.bluetape4k.io.serializer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Kryo Serializer
 */
class KryoSerializer(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractBinarySerializer() {

    override fun doSerialize(graph: Any): ByteArray {
        return ByteArrayOutputStream(bufferSize).use { bos ->
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
