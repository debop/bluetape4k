package io.bluetape4k.data.javers.codecs

import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.gson.JsonObject
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.json.jackson.readValueOrNull
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug

class JacksonCdoShapshotCodec(
    private val mapper: JsonMapper = Jackson.defaultJsonMapper,
): CdoSnapshotCodec<String> {

    companion object: KLogging()

    override fun encode(jsonElement: JsonObject): String {
        val map: Map<String, Any?> = GsonElementConverter.fromJsonObject(jsonElement)
        log.debug { "Encode snapshot ..." }
        log.debug { map.toList().joinToString("\n") }
        return mapper.writeValueAsString(map)
    }

    override fun decode(encodedData: String): JsonObject? {
        val map = mapper.readValueOrNull<Map<String, Any?>>(encodedData)
        return map?.let {
            log.debug { "Decode snapshot..." }
            log.debug { it.toList().joinToString("\n") }
            GsonElementConverter.toJsonObject(map)
        }
    }
}
