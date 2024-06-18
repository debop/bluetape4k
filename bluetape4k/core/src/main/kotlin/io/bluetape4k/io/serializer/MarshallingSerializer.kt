package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import okio.Buffer
import org.jboss.marshalling.InputStreamByteInput
import org.jboss.marshalling.MarshallerFactory
import org.jboss.marshalling.Marshalling
import org.jboss.marshalling.MarshallingConfiguration
import org.jboss.marshalling.OutputStreamByteOutput
import java.util.*

/**
 * JBoss Marshaller 를 이용한 [BinarySerializer]
 *
 * @property factory        [MarshallerFactory]
 * @property configuration  [MarshallingConfiguration]
 */

class MarshallingSerializer private constructor(
    private val factory: MarshallerFactory,
    private val configuration: MarshallingConfiguration,
): AbstractBinarySerializer() {

    enum class Protocol {
        SERIAL,
        RIVER
    }

    companion object: KLogging() {

        @JvmField
        val DefaultMarshallingConfiguration = MarshallingConfiguration()
            .apply {
                instanceCount = 256
                classCount = 64
                bufferSize = 8192
            }

        @JvmStatic
        operator fun invoke(
            protocol: Protocol = Protocol.RIVER,
            configuration: MarshallingConfiguration = DefaultMarshallingConfiguration,
        ): MarshallingSerializer {
            val factory = Marshalling
                .getProvidedMarshallerFactory(protocol.name.lowercase(Locale.ENGLISH))
                ?: throw IllegalArgumentException("Invalid protocol. $protocol")
            return MarshallingSerializer(factory, configuration)
        }
    }

    private val bufferSize: Int get() = configuration.bufferSize


    override fun doSerialize(graph: Any): ByteArray {
        factory.createMarshaller(configuration).use { marshaller ->
            val output = Buffer()
            OutputStreamByteOutput(output.outputStream()).use { osbo ->
                marshaller.start(osbo)
                marshaller.writeObject(graph)
                marshaller.flush()
            }
            return output.readByteArray()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        if (bytes.isEmpty()) {
            return null
        }
        factory.createUnmarshaller(configuration).use { unmarshaller ->
            val input = Buffer().write(bytes)
            InputStreamByteInput(input.inputStream()).use { inputStream ->
                unmarshaller.start(inputStream)
                return unmarshaller.readObject() as? T
            }
        }
    }
}
