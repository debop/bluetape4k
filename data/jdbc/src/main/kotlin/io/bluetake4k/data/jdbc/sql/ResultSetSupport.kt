package io.bluetake4k.data.jdbc.sql

import io.bluetape4k.collections.eclipse.toFastList
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.Blob
import java.sql.Clob
import java.sql.Date
import java.sql.NClob
import java.sql.Ref
import java.sql.ResultSet
import java.sql.RowId
import java.sql.SQLException
import java.sql.SQLXML
import java.sql.Time
import java.sql.Timestamp


/**
 * column index 를 이용하여 column 값을 가져옵니다.
 */
operator fun ResultSet.get(columnIndex: Int): Any? = retrieveValueOrNull(this.getObject(columnIndex))

/**
 * column label 를 이용하여 column 값을 가져옵니다.
 */
operator fun ResultSet.get(columnLabel: String): Any? = retrieveValueOrNull(this.getObject(columnLabel))

/**
 * [ResultSet]으로부터 정보를 읽어, 객체를 생성합니다.
 *
 * ```kotlin
 * val users = statement.executeQuery(sql) { rs: ResultSet ->
 *      rs.extract {
 *          User(long["id"], string["name"])
 *      }
 * }
 * ```
 *
 * @param T
 * @param body
 * @return
 */
inline fun <T> ResultSet.extract(crossinline body: ResultSetGetColumnTokens.() -> T): List<T> {
    val rs = ResultSetGetColumnTokens(this)
    return rs.map { body(rs) }
}

/**
 * [ResultSet] 작업 시 [SQLException]이 발생하면 null을 반환하도록 합니다.
 *
 * @param T  결과 값의 수형
 * @param body ResultSet을 읽어 객체를 생성하는 코드
 * @return 객체
 */
inline fun <T> ResultSet.emptyResultToNull(body: (ResultSet) -> T): T? =
    try {
        body(this)
    } catch (e: SQLException) {
        null
    }


operator fun ResultSet.iterator(): Iterator<ResultSet> {
    val rs = this
    return object: Iterator<ResultSet> {
        override operator fun hasNext(): Boolean = rs.next()
        override operator fun next(): ResultSet = rs
    }
}

inline fun <T> ResultSet.iterator(crossinline mapper: (ResultSet) -> T): Iterator<T> {
    val rs = this
    return object: Iterator<T> {
        override fun hasNext(): Boolean = rs.next()
        override fun next(): T = mapper(rs)
    }
}

inline fun <T> ResultSet.map(crossinline mapper: ResultSet.() -> T): List<T> =
    Iterable { this@map.iterator(mapper) }.toFastList()

val ResultSet.columnNames: List<String>
    get() {
        val meta = this.metaData
        return List(meta.columnCount) { meta.getColumnName(it + 1) ?: it.toString() }
    }

private fun ResultSet.ensureHasRow(): ResultSet = apply {
    if (!this.next()) {
        error("There are no rows left in cursor.")
    }
}

fun ResultSet.singleInt(): Int = this.ensureHasRow().getInt(1)
fun ResultSet.singleLong(): Long = this.ensureHasRow().getLong(1)
fun ResultSet.singleDouble(): Double = this.ensureHasRow().getDouble(1)


private fun <T> ResultSet.retrieveValueOrNull(columnValue: T): T? = if (this.wasNull()) null else columnValue

fun ResultSet.getBooleanOrNull(columnIndex: Int): Boolean? = retrieveValueOrNull(this.getBoolean(columnIndex))
fun ResultSet.getBooleanOrNull(columnLabel: String): Boolean? = retrieveValueOrNull(this.getBoolean(columnLabel))

fun ResultSet.getByteOrNull(columnIndex: Int): Byte? = retrieveValueOrNull(this.getByte(columnIndex))
fun ResultSet.getByteOrNull(columnLabel: String): Byte? = retrieveValueOrNull(this.getByte(columnLabel))

fun ResultSet.getShortOrNull(columnIndex: Int): Short? = retrieveValueOrNull(this.getShort(columnIndex))
fun ResultSet.getShortOrNull(columnLabel: String): Short? = retrieveValueOrNull(this.getShort(columnLabel))

fun ResultSet.getIntOrNull(columnIndex: Int): Int? = retrieveValueOrNull(this.getInt(columnIndex))
fun ResultSet.getIntOrNull(columnLabel: String): Int? = retrieveValueOrNull(this.getInt(columnLabel))

fun ResultSet.getLongOrNull(columnIndex: Int): Long? = retrieveValueOrNull(this.getLong(columnIndex))
fun ResultSet.getLongOrNull(columnLabel: String): Long? = retrieveValueOrNull(this.getLong(columnLabel))

fun ResultSet.getFloatOrNull(columnIndex: Int): Float? = retrieveValueOrNull(this.getFloat(columnIndex))
fun ResultSet.getFloatOrNull(columnLabel: String): Float? = retrieveValueOrNull(this.getFloat(columnLabel))

fun ResultSet.getDoubleOrNull(columnIndex: Int): Double? = retrieveValueOrNull(this.getDouble(columnIndex))
fun ResultSet.getDoubleOrNull(columnLabel: String): Double? = retrieveValueOrNull(this.getDouble(columnLabel))

fun ResultSet.getBigDecimalOrNull(columnIndex: Int): BigDecimal? = retrieveValueOrNull(this.getBigDecimal(columnIndex))
fun ResultSet.getBigDecimalOrNull(columnLabel: String): BigDecimal? =
    retrieveValueOrNull(this.getBigDecimal(columnLabel))

fun ResultSet.getBytesOrNull(columnIndex: Int): ByteArray? = retrieveValueOrNull(this.getBytes(columnIndex))
fun ResultSet.getBytesOrNull(columnLabel: String): ByteArray? = retrieveValueOrNull(this.getBytes(columnLabel))

fun ResultSet.getObjectOrNull(columnIndex: Int): Any? = retrieveValueOrNull(this.getObject(columnIndex))
fun ResultSet.getObjectOrNull(columnLabel: String): Any? = retrieveValueOrNull(this.getObject(columnLabel))

fun ResultSet.getArrayOrNull(columnIndex: Int): java.sql.Array? = retrieveValueOrNull(this.getArray(columnIndex))
fun ResultSet.getArrayOrNull(columnLabel: String): java.sql.Array? = retrieveValueOrNull(this.getArray(columnLabel))

fun ResultSet.getDateOrNull(columnIndex: Int): Date? = retrieveValueOrNull(this.getDate(columnIndex))
fun ResultSet.getDateOrNull(columnLabel: String): Date? = retrieveValueOrNull(this.getDate(columnLabel))

fun ResultSet.getTimeOrNull(columnIndex: Int): Time? = retrieveValueOrNull(this.getTime(columnIndex))
fun ResultSet.getTimeOrNull(columnLabel: String): Time? = retrieveValueOrNull(this.getTime(columnLabel))

fun ResultSet.getTimestampOrNull(columnIndex: Int): Timestamp? = retrieveValueOrNull(this.getTimestamp(columnIndex))
fun ResultSet.getTimestampOrNull(columnLabel: String): Timestamp? = retrieveValueOrNull(this.getTimestamp(columnLabel))

fun ResultSet.getAsciiStreamOrNull(columnIndex: Int): InputStream? =
    retrieveValueOrNull(this.getAsciiStream(columnIndex))

fun ResultSet.getAsciiStreamOrNull(columnLabel: String): InputStream? =
    retrieveValueOrNull(this.getAsciiStream(columnLabel))

fun ResultSet.getBinaryStreamOrNull(columnIndex: Int): InputStream? =
    retrieveValueOrNull(this.getBinaryStream(columnIndex))

fun ResultSet.getBinaryStreamOrNull(columnLabel: String): InputStream? =
    retrieveValueOrNull(this.getBinaryStream(columnLabel))

fun ResultSet.getCharacterStreamOrNull(columnIndex: Int): Reader? =
    retrieveValueOrNull(this.getCharacterStream(columnIndex))

fun ResultSet.getCharacterStreamOrNull(columnLabel: String): Reader? =
    retrieveValueOrNull(this.getCharacterStream(columnLabel))

fun ResultSet.getNCharacterStreamOrNull(columnIndex: Int): Reader? =
    retrieveValueOrNull(this.getNCharacterStream(columnIndex))

fun ResultSet.getNCharacterStreamOrNull(columnLabel: String): Reader? =
    retrieveValueOrNull(this.getNCharacterStream(columnLabel))

fun ResultSet.getStringOrNull(columnIndex: Int): String? = retrieveValueOrNull(this.getString(columnIndex))
fun ResultSet.getStringOrNull(columnLabel: String): String? = retrieveValueOrNull(this.getString(columnLabel))

fun ResultSet.getNStringOrNull(columnIndex: Int): String? = retrieveValueOrNull(this.getNString(columnIndex))
fun ResultSet.getNStringOrNull(columnLabel: String): String? = retrieveValueOrNull(this.getNString(columnLabel))

fun ResultSet.getBlobOrNull(columnIndex: Int): Blob? = retrieveValueOrNull(this.getBlob(columnIndex))
fun ResultSet.getBlobOrNull(columnLabel: String): Blob? = retrieveValueOrNull(this.getBlob(columnLabel))

fun ResultSet.getClobOrNull(columnIndex: Int): Clob? = retrieveValueOrNull(this.getClob(columnIndex))
fun ResultSet.getClobOrNull(columnLabel: String): Clob? = retrieveValueOrNull(this.getClob(columnLabel))

fun ResultSet.getNClobOrNull(columnIndex: Int): NClob? = retrieveValueOrNull(this.getNClob(columnIndex))
fun ResultSet.getNClobOrNull(columnLabel: String): NClob? = retrieveValueOrNull(this.getNClob(columnLabel))

fun ResultSet.getSQLXMLOrNull(columnIndex: Int): SQLXML? = retrieveValueOrNull(this.getSQLXML(columnIndex))
fun ResultSet.getSQLXMLOrNull(columnLabel: String): SQLXML? = retrieveValueOrNull(this.getSQLXML(columnLabel))

fun ResultSet.getRefOrNull(columnIndex: Int): Ref? = retrieveValueOrNull(this.getRef(columnIndex))
fun ResultSet.getRefOrNull(columnLabel: String): Ref? = retrieveValueOrNull(this.getRef(columnLabel))

fun ResultSet.getRowIdOrNull(columnIndex: Int): RowId? = retrieveValueOrNull(this.getRowId(columnIndex))
fun ResultSet.getRowIdOrNull(columnLabel: String): RowId? = retrieveValueOrNull(this.getRowId(columnLabel))

fun ResultSet.getURLOrNull(columnIndex: Int): URL? = retrieveValueOrNull(this.getURL(columnIndex))
fun ResultSet.getURLOrNull(columnLabel: String): URL? = retrieveValueOrNull(this.getURL(columnLabel))
