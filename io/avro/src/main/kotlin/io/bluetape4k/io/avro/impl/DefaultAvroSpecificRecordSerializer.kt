package io.bluetape4k.io.avro.impl

import io.bluetape4k.io.avro.AvroSpecificRecordSerializer
import io.bluetape4k.io.avro.DEFAULT_CODEC_FACTORY
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import org.apache.avro.file.CodecFactory
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecord
import java.io.ByteArrayOutputStream

/**
 * Avro Protocol을 이용하여, Data 전송, RCP Call 을 수행할 수 있습니다.
 * 데이터 전송 시, DefaultSpecificRecordSerializer 가 avro instance 를
 * byte array 나 문자열로 변환하고, 수신하는 쪽에서 byte array 나 문자열를 Avro Instance 로 빌드할 수 있습니다
 */
class DefaultAvroSpecificRecordSerializer private constructor(
    private val codecFactory: CodecFactory,
): AvroSpecificRecordSerializer {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            codecFactory: CodecFactory = DEFAULT_CODEC_FACTORY,
        ): DefaultAvroSpecificRecordSerializer {
            return DefaultAvroSpecificRecordSerializer(codecFactory)
        }
    }

    /**
     * Avro [SpecificRecord] 인스턴스를 직렬화하여 [ByteArray]로 반환합니다.
     *
     * @param graph 직렬화할 Avro [SpecificRecord] 객체
     * @return 직렬화된 데이터, 실패 시에는 null을 반환
     */
    override fun <T: SpecificRecord> serialize(graph: T?): ByteArray? {
        if (graph == null) {
            return null
        }

        return try {
            val sdw = SpecificDatumWriter<T>(graph.schema)
            DataFileWriter(sdw).setCodec(codecFactory).use { dfw ->
                ByteArrayOutputStream().use { bos ->
                    dfw.create(graph.schema, bos)
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

    /**
     * Avro [SpecificRecord]의 직렬화된 정보를 역직렬화하여 [class] 형식의 인스턴스를 빌드합니다.
     *
     * @param avroBytes [SpecificRecord]의 직렬화된 정보
     * @param clazz 대상 수형 정보
     * @return 역직렬화된 인스턴스, 실패 시에는 null 반환
     */
    override fun <T: SpecificRecord> deserialize(avroBytes: ByteArray?, clazz: Class<T>): T? {
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
            log.error(e) { "Fail to deserialize avro instance. clazz=$clazz," }
            null
        }
    }
}
