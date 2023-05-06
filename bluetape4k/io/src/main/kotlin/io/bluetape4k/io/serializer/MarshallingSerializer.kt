package io.bluetape4k.io.serializer

import io.bluetape4k.logging.KLogging
import org.jboss.marshalling.InputStreamByteInput
import org.jboss.marshalling.MarshallerFactory
import org.jboss.marshalling.Marshalling
import org.jboss.marshalling.MarshallingConfiguration
import org.jboss.marshalling.OutputStreamByteOutput
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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
        val DefaultMarshallingConfiguration = MarshallingConfiguration().apply {
            instanceCount = 256
            classCount = 64
            bufferSize = 1024
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


    override fun doSerialize(graph: Any): ByteArray {
        return factory.createMarshaller(configuration).use { marshaller ->
            ByteArrayOutputStream(DEFAULT_BUFFER_SIZE).use { bos ->
                OutputStreamByteOutput(bos).use { osbo ->
                    marshaller.start(osbo)
                    marshaller.writeObject(graph)
                    marshaller.flush()
                }
                bos.toByteArray()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> doDeserialize(bytes: ByteArray): T? {
        return factory.createUnmarshaller(configuration).use { unmarshaller ->
            ByteArrayInputStream(bytes).buffered(DEFAULT_BUFFER_SIZE).use { bis ->
                InputStreamByteInput(bis).use { inputStream ->
                    unmarshaller.start(inputStream)
                    unmarshaller.readObject() as? T
                }
            }
        }
    }
}
