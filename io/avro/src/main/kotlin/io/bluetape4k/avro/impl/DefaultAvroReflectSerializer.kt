package io.bluetape4k.avro.impl

import io.bluetape4k.avro.AvroReflectSerializer
import io.bluetape4k.avro.DEFAULT_CODEC_FACTORY
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import org.apache.avro.file.CodecFactory
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.reflect.ReflectDatumWriter
import org.apache.avro.specific.SpecificDatumReader
import java.io.ByteArrayOutputStream

class DefaultAvroReflectSerializer private constructor(
    private val codecFactory: CodecFactory,
): AvroReflectSerializer {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            codecFactory: CodecFactory = DEFAULT_CODEC_FACTORY,
        ): DefaultAvroReflectSerializer {
            return DefaultAvroReflectSerializer(codecFactory)
        }
    }

    override fun <T> serialize(graph: T?): ByteArray? {
        if (graph == null) {
            return null
        }

        return try {
            val rdw = ReflectDatumWriter(graph.javaClass)
            DataFileWriter(rdw).setCodec(codecFactory).use { dfw ->
                ByteArrayOutputStream().use { bos ->
                    dfw.create(rdw.specificData.getSchema(graph.javaClass), bos)
                    dfw.append(graph)
                    dfw.flush()

                    bos.toByteArray()
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Fail to serialize avro instance. graph=$graph" }
            null
        }
    }

    override fun <T> deserialize(avroBytes: ByteArray?, clazz: Class<T>): T? {
        if (avroBytes == null) {
            return null
        }

        return try {
            SeekableByteArrayInput(avroBytes).use { sin ->
                val sdr = SpecificDatumReader(clazz)
                DataFileReader(sin, sdr).use { dfr ->
                    if (dfr.hasNext()) dfr.next()
                    else null
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Fail to deserialize avro instance. clazz=$clazz" }
            null
        }
    }
}
