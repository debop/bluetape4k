package io.bluetape4k.cassandra.cql

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.type.codec.TypeCodec
import io.bluetape4k.support.EMPTY_STRING


fun Row.getStringOrEmpty(i: Int): String = getString(i) ?: EMPTY_STRING
fun Row.getStringOrEmpty(name: String): String = getString(name) ?: EMPTY_STRING
fun Row.getStringOrEmpty(id: CqlIdentifier): String = getString(id) ?: EMPTY_STRING

fun Row.toMap(): Map<Int, Any?> {
    return columnDefinitions
        .mapIndexed { i, definition ->
            val codec = codecRegistry().codecFor<Any?>(definition.type)
            val value = if (isNull(definition.name)) null else codec.decode(getBytesUnsafe(i), protocolVersion())

            i to value
        }
        .toMap()
}

fun Row.toNamedMap(): Map<String, Any?> {
    return columnDefinitions
        .mapIndexed { i, definition ->
            val name = definition.name.asCql(true)
            val codec = codecRegistry().codecFor<Any?>(definition.type)
            val value = if (isNull(definition.name)) null else codec.decode(getBytesUnsafe(i), protocolVersion())

            name to value
        }
        .toMap()
}

inline fun <T> Row.map(transform: (Any?) -> T): Map<Int, T> {
    return columnDefinitions
        .mapIndexed { i, definition ->
            val codec = codecRegistry().codecFor<Any?>(definition.type)
            val value = if (isNull(definition.name)) null else codec.decode(getBytesUnsafe(i), protocolVersion())

            i to transform(value)
        }
        .toMap()
}

inline fun <T> Row.mapWithName(transform: (Any?) -> T): Map<String, T> {
    return columnDefinitions
        .mapIndexed { i, definition ->
            val name = definition.name.asCql(true)
            val codec = codecRegistry().codecFor<Any?>(definition.type)
            val value = if (isNull(definition.name)) null else codec.decode(getBytesUnsafe(i), protocolVersion())
            name to transform(value)
        }
        .toMap()
}

fun Row.toCqlIdentifierMap(): Map<CqlIdentifier, Any?> {
    return columnDefinitions
        .mapIndexed { i, definition ->
            val name = definition.name
            val codec = codecRegistry().codecFor<Any?>(definition.type)
            val value = if (isNull(name)) null else codec.decode(getBytesUnsafe(i), protocolVersion())

            name to value
        }
        .toMap()
}

fun Row.columnCodecs(): Map<CqlIdentifier, TypeCodec<out Any>> {
    val codecRegistry = codecRegistry()
    return columnDefinitions
        .associate { columnDef ->
            val identifier = columnDef.name
            val codec = codecRegistry.codecFor<Any?>(columnDef.type)
            identifier to codec
        }
}
