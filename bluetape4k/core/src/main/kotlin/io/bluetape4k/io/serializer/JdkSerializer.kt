package io.bluetape4k.io.serializer

import io.bluetape4k.io.ApacheByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Java Built-in Serializer
 *
 * @see ObjectOutputStream
 * @see ObjectInputStream
 */
class JdkSerializer(
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
): AbstractBinarySerializer() {

    override fun doSerialize(graph: Any): ByteArray {
        return ApacheByteArrayOutputStream(bufferSize).use { bos ->
            ObjectOutputStream(bos).use { oos ->
                oos.writeObject(graph)
                oos.flush()
            }
            bos.toByteArray()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isEmpty()) {
            return null
        }

        return ByteArrayInputStream(bytes).buffered(bufferSize).use { bis ->
            ObjectInputStream(bis).use { ois ->
                ois.readObject() as? T
            }
        }
    }
}
