package io.bluetape4k.javers.codecs

import com.google.gson.JsonObject

interface CdoSnapshotCodec<T: Any> {

    fun encode(jsonElement: JsonObject): T

    fun decode(encodedData: T): JsonObject?

}
