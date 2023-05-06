package io.bluetape4k.io.avro.impl

import io.bluetape4k.io.avro.AvroGenericRecordSerializer
import io.bluetape4k.io.avro.DEFAULT_CODEC_FACTORY
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.error
import org.apache.avro.Schema
import org.apache.avro.file.CodecFactory
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import java.io.ByteArrayOutputStream

class DefaultAvroGenericRecordSerializer(
    private val codecFactory: CodecFactory,
): AvroGenericRecordSerializer {

    companion object: KLogging() {
        operator fun invoke(
            codecFactory: CodecFactory = DEFAULT_CODEC_FACTORY,
        ): DefaultAvroGenericRecordSerializer {
            return DefaultAvroGenericRecordSerializer(codecFactory)
        }
    }

    /**
     * [graph]를 Avro로 직렬화를 수행합니다.
     *
     * @param graph 직렬화할 Avro 객체
     * @param schema Avro 객체의 [Schema] 정보
     * @return 직렬화된 [ByteArray], 실패 시에는 null을 반환
     */
    override fun serialize(schema: Schema, graph: GenericRecord?): ByteArray? {
        if (graph == null) {
            return null
        }

        return try {
            val datumWriter = GenericDatumWriter<GenericRecord>(schema)
            DataFileWriter(datumWriter).setCodec(codecFactory).use { dfw ->
                ByteArrayOutputStream().use { bos ->
                    dfw.create(schema, bos)
                    dfw.append(graph)
                    dfw.flush()

                    bos.toByteArray()
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Fail to serialize avro instance. graph=$graph, schema=$schema" }
            null
        }
    }

    /**
     * Avro 직렬화된 정보를 Avro [Record]로 역직렬화합니다.
     *
     * @param avroBytes 직렬화된 데이터
     * @param schema Avro 객체의 [Schema] 정보
     * @return 역직렬화된 Avro [Record], 실패 시에는 null 반환
     */
    override fun deserialize(schema: Schema, avroBytes: ByteArray?): GenericData.Record? {
        if (avroBytes == null) {
            return null
        }

        return try {
            SeekableByteArrayInput(avroBytes).use { sin ->
                val datumReader = GenericDatumReader<GenericData.Record>(schema)
                DataFileReader(sin, datumReader).use { reader ->
                    if (reader.hasNext()) reader.next()
                    else null
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Fail to deserialize avro instance. avroBytes=$avroBytes, schema=$schema" }
            null
        }
    }
}
