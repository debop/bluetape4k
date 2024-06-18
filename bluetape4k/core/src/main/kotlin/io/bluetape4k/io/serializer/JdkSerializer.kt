package io.bluetape4k.io.serializer

import okio.Buffer
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
        val output = Buffer()
        ObjectOutputStream(output.outputStream()).use { oos ->
            oos.writeObject(graph)
            oos.flush()
        }
        return output.readByteArray()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isEmpty()) {
            return null
        }
        val input = Buffer().write(bytes)
        ObjectInputStream(input.inputStream()).use { ois ->
            return ois.readObject() as? T
        }
    }
}
