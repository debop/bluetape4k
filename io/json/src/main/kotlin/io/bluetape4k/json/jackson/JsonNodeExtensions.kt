package io.bluetape4k.json.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import java.math.BigDecimal
import java.math.BigInteger

fun JsonNode.createNode(fieldName: String?): JsonNode = when (this) {
    is ObjectNode -> putObject(fieldName)
    is ArrayNode  -> addObject()
    else          -> JsonNodeFactory.instance.objectNode()
}

fun JsonNode.createArray(fieldName: String?): JsonNode = when (this) {
    is ObjectNode -> putArray(fieldName)
    is ArrayNode  -> addArray()
    else          -> JsonNodeFactory.instance.arrayNode()
}

fun JsonNode.addLong(value: Long, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addInt(value: Int, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addString(value: String, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addFloat(value: Float, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addDouble(value: Double, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addFloat(value: BigDecimal, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addFloat(value: BigInteger, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addShort(value: Short, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addBoolean(value: Boolean, fieldName: String?) {
    when (this) {
        is ObjectNode -> put(fieldName, value)
        is ArrayNode  -> add(value)
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}

fun JsonNode.addNull(fieldName: String?) {
    when (this) {
        is ObjectNode -> putNull(fieldName)
        is ArrayNode  -> addNull()
        else          -> throw RuntimeException("Unknown json node type. ${this.nodeType}")
    }
}
