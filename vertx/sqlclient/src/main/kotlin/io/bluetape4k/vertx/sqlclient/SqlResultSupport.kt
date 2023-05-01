package io.bluetape4k.vertx.sqlclient

import io.vertx.jdbcclient.JDBCPool
import io.vertx.mysqlclient.MySQLConnection
import io.vertx.sqlclient.*

/**
 * 최신의 Auto Increment Identifier 값을 가져온다
 *
 * @param ID         identifier type
 * @param client     sql client
 * @param columnName identifier column name
 * @return identifier value or null
 */
inline fun <reified ID : Number> SqlResult<*>.getGeneratedId(client: SqlConnection, columnName: String = "id"): ID? {
    return when (client) {
        is MySQLConnection -> {
            val lastInsertedId = PropertyKind.create("last-inserted-id", ID::class.java)
            this.property(lastInsertedId)
        }

        else -> this.property(JDBCPool.GENERATED_KEYS).getValue(columnName) as? ID
    }
}

fun Row.hasColumn(index: Int): Boolean = index in 0 until size()
fun Row.hasColumn(columnName: String): Boolean = hasColumn(getColumnIndex(columnName))

inline fun <reified T : Any> Row.valueAs(columnName: String): T? {
    return getValue(columnName) as? T
}

fun Row.getValueOrNull(columnName: String): Any? {
    return if (hasColumn(columnName)) getValue(columnName) else null
}

fun Row.getIntOrNull(columnName: String): Int? {
    return if (hasColumn(columnName)) getInteger(columnName) else null
}

fun Row.getLongOrNull(columnName: String): Long? {
    return if (hasColumn(columnName)) getLong(columnName) else null
}

fun Row.getStringOrNull(columnName: String): String? {
    return if (hasColumn(columnName)) getString(columnName) else null
}

fun Row.getJsonOrNull(columnName: String): Any? {
    return if (hasColumn(columnName)) getJson(columnName) else null
}

fun Row.jsonEncode(): String = toJson().encode()

fun RowSet<Row>.jsonEncode(): String = joinToString { it.jsonEncode() }
