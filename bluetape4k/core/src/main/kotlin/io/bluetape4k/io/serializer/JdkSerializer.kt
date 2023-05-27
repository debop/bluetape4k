package io.bluetape4k.io.serializer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Java Built-in Serializer
 */
class JdkSerializer: AbstractBinarySerializer() {

    override fun doSerialize(graph: Any): ByteArray {
        return ByteArrayOutputStream(DEFAULT_BUFFER_SIZE).use { bos ->
            ObjectOutputStream(bos).use { oos ->
                oos.writeObject(graph)
                oos.flush()
            }
            bos.toByteArray()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        return ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { ois ->
                ois.readObject() as? T
            }
        }
    }
}
