package io.bluetape4k.json.jackson

import com.fasterxml.jackson.core.JsonGenerator

inline fun JsonGenerator.writeValue(writeValueAction: JsonGenerator.() -> Unit) {
    writeStartObject()
    writeValueAction()
    writeEndObject()
}

inline fun JsonGenerator.writeValue(fieldName: String, writeValueAction: JsonGenerator.() -> Unit) {
    writeValue {
        writeFieldName(fieldName)
        writeValueAction()
    }
}

fun JsonGenerator.writeValue(fieldName: String, value: Any?) {
    writeValue(fieldName) {
        value?.let { writeString(it.toString()) } ?: writeNull()
    }
}

fun JsonGenerator.writeNull(fieldName: String) {
    writeValue {
        writeNull(fieldName)
    }
}

fun JsonGenerator.writeString(fieldName: String, value: String?) {
    writeValue(fieldName) {
        value?.let { writeString(it) } ?: writeNull()
    }
}

fun JsonGenerator.writeNumber(fieldName: String, value: Number) {
    writeValue(fieldName) {
        writeNumber(value.toLong())
    }
}

inline fun JsonGenerator.writeArray(writeArrayAction: JsonGenerator.() -> Unit) {
    writeStartArray()
    writeArrayAction()
    writeEndArray()
}

fun <T> JsonGenerator.writeObjects(items: Iterable<T>) {
    writeArray {
        items.forEach { writeObject(it) }
    }
}
