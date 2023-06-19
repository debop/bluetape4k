package io.bluetape4k.data.javers.codecs

import com.google.gson.JsonObject
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace

class BinaryCdoSnapshotCodec(
    private val serializer: BinarySerializer,
): CdoSnapshotCodec<ByteArray> {

    companion object: KLogging()

    override fun encode(jsonElement: JsonObject): ByteArray {
        val map = GsonElementConverter.fromJsonObject(jsonElement)
        log.trace { map.toList().joinToString("\n") }
        return serializer.serialize(map)
    }

    override fun decode(encodedData: ByteArray): JsonObject? {
        val map = serializer.deserialize<Map<String, Any?>>(encodedData)
        return map?.let {
            log.trace { it.toList().joinToString("\n") }
            GsonElementConverter.toJsonObject(it)
        }
    }
}
