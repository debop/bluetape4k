package io.bluetape4k.data.javers.codecs

import com.google.gson.JsonObject
import io.bluetape4k.io.serializer.BinarySerializer
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug

class BinaryCdoSnapshotCodec(
    private val serializer: BinarySerializer,
): AbstractCdoSnapshotCodec<ByteArray>() {

    companion object: KLogging()

    override fun encode(jsonElement: JsonObject): ByteArray {
        val map = toMap(jsonElement)
        log.debug {
            map.toList().joinToString("\n")
        }
        return serializer.serialize(map)
    }

    override fun decode(encodedData: ByteArray): JsonObject? {
        val map = serializer.deserialize<Map<String, Any?>>(encodedData)
        return map?.let {
            log.debug {
                it.toList().joinToString("\n")
            }
            fromMap(it)
        }
    }
}
