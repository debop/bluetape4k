package io.bluetape4k.io.serializer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Kryo Serializer
 */
class KryoSerializer: AbstractBinarySerializer() {

    override fun doSerialize(graph: Any): ByteArray {
        return ByteArrayOutputStream(DEFAULT_BUFFER_SIZE).use { bos ->
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
        return withKryoInput { input ->
            input.inputStream = ByteArrayInputStream(bytes).buffered(DEFAULT_BUFFER_SIZE)
            withKryo {
                readClassAndObject(input) as? T
            }
        }
    }
}
