package io.bluetape4k.io.json.gson

import com.google.gson.Gson
import io.bluetape4k.io.json.JsonSerializer
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String

class GsonSerializer(private val gson: Gson = DefaultGson): JsonSerializer {

    override fun serialize(graph: Any?): ByteArray {
        return graph?.run { gson.toJson(this).toUtf8Bytes() } ?: emptyByteArray
    }

    override fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        return bytes?.run { gson.fromJson(bytes.toUtf8String(), clazz) }
    }

}
