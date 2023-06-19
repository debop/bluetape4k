package io.bluetape4k.data.javers.codecs

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.bluetape4k.logging.KLogging
import java.math.BigDecimal

interface CdoSnapshotCodec<T: Any> {

    fun encode(jsonElement: JsonObject): T

    fun decode(encodedData: T): JsonObject?

}

abstract class AbstractCdoSnapshotCodec<T: Any>: CdoSnapshotCodec<T> {

    companion object: KLogging() {
        @JvmStatic
        protected fun toMap(jsonObject: JsonObject): Map<String, Any?> {
            val map = hashMapOf<String, Any?>()
            jsonObject.entrySet().forEach { (key, jsonElement) ->
                map[key] = fromJsonElement(jsonElement)
            }
            return map
        }

        @JvmStatic
        protected fun fromMap(map: Map<String, Any?>): JsonObject {
            val jsonObject = JsonObject()
            map.forEach { key, value ->
                jsonObject.add(key, toJsonElement(value))
            }
            return jsonObject
        }

        @JvmStatic
        private fun fromJsonElement(element: JsonElement): Any? {
            if (element == JsonNull.INSTANCE) {
                return null
            }
            if (element is JsonObject) {
                return toMap(element)
            }
            if (element is JsonPrimitive) {
                if (element.isString) {
                    return element.asString
                }
                if (element.isNumber && element.asNumber is BigDecimal) {
                    val value = element.asNumber as BigDecimal
                    return try {
                        value.longValueExact()
                    } catch (e: ArithmeticException) {
                        value.toDouble()
                    }
                }
                if (element.isNumber) {
                    return element.asNumber
                }
                if (element.isBoolean) {
                    return element.asBoolean
                }
            }
            if (element is JsonArray) {
                return mutableListOf<Any?>().apply {
                    element.forEach { elem ->
                        add(fromJsonElement(elem))
                    }
                }
            }

            throw IllegalArgumentException("unsupported JsonElement type - ${element.javaClass.simpleName}")
        }

        @JvmStatic
        private fun toJsonElement(value: Any?): JsonElement {
            if (value == null) {
                return JsonNull.INSTANCE
            }
            if (value is Map<*, *>) {
                return fromMap(value as Map<String, Any?>)
            }
            if (value is String) {
                return JsonPrimitive(value)
            }
            if (value is Number) {
                return JsonPrimitive(value)
            }
            if (value is Boolean) {
                return JsonPrimitive(value)
            }
            if (value is List<*>) {
                return JsonArray().apply {
                    value.forEach { item ->
                        add(toJsonElement(item))
                    }
                }
            }
            throw IllegalArgumentException("unsupported object type - ${value.javaClass.simpleName}")
        }
    }
}
