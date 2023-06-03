package io.bluetape4k.data.r2dbc.support

import io.bluetape4k.support.asBigDecimal
import io.bluetape4k.support.asBigDecimalOrNull
import io.bluetape4k.support.asBooleanOrNull
import io.bluetape4k.support.asByteArray
import io.bluetape4k.support.asByteArrayOrNull
import io.bluetape4k.support.asByteOrNull
import io.bluetape4k.support.asChar
import io.bluetape4k.support.asCharOrNull
import io.bluetape4k.support.asDate
import io.bluetape4k.support.asDateOrNull
import io.bluetape4k.support.asDouble
import io.bluetape4k.support.asDoubleOrNull
import io.bluetape4k.support.asFloat
import io.bluetape4k.support.asFloatOrNull
import io.bluetape4k.support.asInstant
import io.bluetape4k.support.asInstantOrNull
import io.bluetape4k.support.asIntOrNull
import io.bluetape4k.support.asLocalDate
import io.bluetape4k.support.asLocalDateOrNull
import io.bluetape4k.support.asLocalDateTime
import io.bluetape4k.support.asLocalDateTimeOrNull
import io.bluetape4k.support.asLocalTime
import io.bluetape4k.support.asLocalTimeOrNull
import io.bluetape4k.support.asLong
import io.bluetape4k.support.asLongOrNull
import io.bluetape4k.support.asOffsetDateTime
import io.bluetape4k.support.asOffsetDateTimeOrNull
import io.bluetape4k.support.asShortOrNull
import io.bluetape4k.support.asString
import io.bluetape4k.support.asStringOrNull
import io.bluetape4k.support.asTimestamp
import io.bluetape4k.support.asTimestampOrNull
import io.bluetape4k.support.asUUIDOrNull
import io.r2dbc.spi.Row
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

inline fun <reified T: Any> Row.getAs(index: Int): T = get(index, T::class.java)!!
inline fun <reified T: Any> Row.getAs(name: String): T = get(name, T::class.java)!!
inline fun <reified T: Any> Row.getAsOrNull(index: Int): T? = get(index, T::class.java)
inline fun <reified T: Any> Row.getAsOrNull(name: String): T? = get(name, T::class.java)

fun Row.boolean(index: Int): Boolean = getAs<Boolean>(index)
fun Row.boolean(name: String): Boolean = getAs<Boolean>(name)
fun Row.booleanOrNull(index: Int): Boolean? = get(index).asBooleanOrNull()
fun Row.booleanOrNull(name: String): Boolean? = get(name).asBooleanOrNull()

fun Row.char(index: Int): Char = get(index).asChar()
fun Row.char(name: String): Char = get(name).asChar()
fun Row.charOrNull(index: Int): Char? = get(index).asCharOrNull()
fun Row.charOrNull(name: String): Char? = get(name).asCharOrNull()

fun Row.byte(index: Int): Byte = getAs<Byte>(index)
fun Row.byte(name: String): Byte = getAs<Byte>(name)
fun Row.byteOrNull(index: Int): Byte? = get(index).asByteOrNull()
fun Row.byteOrNull(name: String): Byte? = get(name).asByteOrNull()

fun Row.short(index: Int): Short = getAs<Short>(index)
fun Row.short(name: String): Short = getAs<Short>(name)
fun Row.shortOrNull(index: Int): Short? = get(index).asShortOrNull()
fun Row.shortOrNull(name: String): Short? = get(name).asShortOrNull()

fun Row.int(index: Int): Int = getAs<Int>(index)
fun Row.int(name: String): Int = getAs<Int>(name)
fun Row.intOrNull(index: Int): Int? = get(index).asIntOrNull()
fun Row.intOrNull(name: String): Int? = get(name).asIntOrNull()

fun Row.long(index: Int): Long = get(index).asLong()
fun Row.long(name: String): Long = get(name).asLong()
fun Row.longOrNull(index: Int): Long? = get(index).asLongOrNull()
fun Row.longOrNull(name: String): Long? = get(name).asLongOrNull()

fun Row.float(index: Int): Float = get(index).asFloat()
fun Row.float(name: String): Float = get(name).asFloat()
fun Row.floatOrNull(index: Int): Float? = get(index).asFloatOrNull()
fun Row.floatOrNull(name: String): Float? = get(name).asFloatOrNull()

fun Row.double(index: Int): Double = get(index).asDouble()
fun Row.double(name: String): Double = get(name).asDouble()
fun Row.doubleOrNull(index: Int): Double? = get(index).asDoubleOrNull()
fun Row.doubleOrNull(name: String): Double? = get(name).asDoubleOrNull()

fun Row.bigDecimal(index: Int): BigDecimal = get(index).asBigDecimal()
fun Row.bigDecimal(name: String): BigDecimal = get(name).asBigDecimal()
fun Row.bigDecimalOrNull(index: Int): BigDecimal? = get(index).asBigDecimalOrNull()
fun Row.bigDecimalOrNull(name: String): BigDecimal? = get(name).asBigDecimalOrNull()

fun Row.string(index: Int): String = get(index, String::class.java).asString()
fun Row.string(name: String): String = get(name, String::class.java).asString()
fun Row.stringOrNull(index: Int): String? = getAsOrNull<String>(index).asStringOrNull()
fun Row.stringOrNull(name: String): String? = getAsOrNull<String>(name).asStringOrNull()

fun Row.byteArray(index: Int): ByteArray = get(index).asByteArray()
fun Row.byteArray(name: String): ByteArray = get(name).asByteArray()
fun Row.byteArrayOrNull(index: Int): ByteArray? = get(index).asByteArrayOrNull()
fun Row.byteArrayOrNull(name: String): ByteArray? = get(name).asByteArrayOrNull()

fun Row.date(index: Int): Date = get(index).asDate()
fun Row.date(name: String): Date = get(name).asDate()
fun Row.dateOrNull(index: Int): Date? = get(index).asDateOrNull()
fun Row.dateOrNull(name: String): Date? = get(name).asDateOrNull()

fun Row.timestamp(index: Int): Timestamp = get(index).asTimestamp()
fun Row.timestamp(name: String): Timestamp = get(name).asTimestamp()
fun Row.timestampOrNull(index: Int): Timestamp? = get(index).asTimestampOrNull()
fun Row.timestampOrNull(name: String): Timestamp? = get(name).asTimestampOrNull()

fun Row.instant(index: Int): Instant = get(index).asInstant()
fun Row.instant(name: String): Instant = get(name).asInstant()
fun Row.instantOrNull(index: Int): Instant? = get(index).asInstantOrNull()
fun Row.instantOrNull(name: String): Instant? = get(name).asInstantOrNull()

fun Row.localDate(index: Int): LocalDate = get(index).asLocalDate()
fun Row.localDate(name: String): LocalDate = get(name).asLocalDate()
fun Row.localDateOrNull(index: Int): LocalDate? = get(index).asLocalDateOrNull()
fun Row.localDateOrNull(name: String): LocalDate? = get(name).asLocalDateOrNull()

fun Row.localTime(index: Int): LocalTime = get(index).asLocalTime()
fun Row.localTime(name: String): LocalTime = get(name).asLocalTime()
fun Row.localTimeOrNull(index: Int): LocalTime? = get(index).asLocalTimeOrNull()
fun Row.localTimeOrNull(name: String): LocalTime? = get(name).asLocalTimeOrNull()

fun Row.localDateTime(index: Int): LocalDateTime = get(index).asLocalDateTime()
fun Row.localDateTime(name: String): LocalDateTime = get(name).asLocalDateTime()
fun Row.localDateTimeOrNull(index: Int): LocalDateTime? = get(index).asLocalDateTimeOrNull()
fun Row.localDateTimeOrNull(name: String): LocalDateTime? = get(name).asLocalDateTimeOrNull()

fun Row.offsetDateTime(index: Int): OffsetDateTime = get(index).asOffsetDateTime()
fun Row.offsetDateTime(name: String): OffsetDateTime = get(name).asOffsetDateTime()
fun Row.offsetDateTimeOrNull(index: Int): OffsetDateTime? = get(index).asOffsetDateTimeOrNull()
fun Row.offsetDateTimeOrNull(name: String): OffsetDateTime? = get(name).asOffsetDateTimeOrNull()

fun Row.uuid(index: Int): UUID = getAs<UUID>(index)
fun Row.uuid(name: String): UUID = getAs<UUID>(name)
fun Row.uuidOrNull(index: Int): UUID? = get(index).asUUIDOrNull()
fun Row.uuidOrNull(name: String): UUID? = get(name).asUUIDOrNull()
