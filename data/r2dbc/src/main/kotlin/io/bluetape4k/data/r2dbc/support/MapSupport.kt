package io.bluetape4k.data.r2dbc.support

import io.bluetape4k.support.asBigDecimal
import io.bluetape4k.support.asBigDecimalOrNull
import io.bluetape4k.support.asBoolean
import io.bluetape4k.support.asBooleanOrNull
import io.bluetape4k.support.asByte
import io.bluetape4k.support.asByteArray
import io.bluetape4k.support.asByteArrayOrNull
import io.bluetape4k.support.asByteOrNull
import io.bluetape4k.support.asChar
import io.bluetape4k.support.asCharOrNull
import io.bluetape4k.support.asDate
import io.bluetape4k.support.asDateOrNull
import io.bluetape4k.support.asInstant
import io.bluetape4k.support.asInstantOrNull
import io.bluetape4k.support.asInt
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
import io.bluetape4k.support.asShort
import io.bluetape4k.support.asShortOrNull
import io.bluetape4k.support.asString
import io.bluetape4k.support.asStringOrNull
import io.bluetape4k.support.asTimestamp
import io.bluetape4k.support.asTimestampOrNull
import io.bluetape4k.support.asUUID
import io.bluetape4k.support.asUUIDOrNull
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.*

fun Map<String, Any?>.boolean(name: String): Boolean = this[name]!!.asBoolean()
fun Map<String, Any?>.booleanOrNull(name: String): Boolean? = this[name].asBooleanOrNull()

fun Map<String, Any?>.char(name: String): Char = this[name]!!.asChar()
fun Map<String, Any?>.charOrNull(name: String): Char? = this[name].asCharOrNull()

fun Map<String, Any?>.byte(name: String): Byte = this[name]!!.asByte()
fun Map<String, Any?>.byteOrNull(name: String): Byte? = this[name].asByteOrNull()

fun Map<String, Any?>.short(name: String): Short = this[name]!!.asShort()
fun Map<String, Any?>.shortOrNull(name: String): Short? = this[name].asShortOrNull()

fun Map<String, Any?>.int(name: String): Int = this[name]!!.asInt()
fun Map<String, Any?>.intOrNull(name: String): Int? = this[name].asIntOrNull()

fun Map<String, Any?>.long(name: String): Long = this[name]!!.asLong()
fun Map<String, Any?>.longOrNull(name: String): Long? = this[name].asLongOrNull()

fun Map<String, Any?>.bigDecimal(name: String): BigDecimal = this[name]!!.asBigDecimal()
fun Map<String, Any?>.bigDecimalOrNull(name: String): BigDecimal? = this[name].asBigDecimalOrNull()

fun Map<String, Any?>.string(name: String): String = this[name]!!.asString()
fun Map<String, Any?>.stringOrNull(name: String): String? = this[name].asStringOrNull()

fun Map<String, Any?>.byteArray(name: String): ByteArray = this[name]!!.asByteArray()
fun Map<String, Any?>.byteArrayOrNull(name: String): ByteArray? = this[name].asByteArrayOrNull()

fun Map<String, Any?>.date(name: String): Date = this[name]!!.asDate()
fun Map<String, Any?>.dateOrNull(name: String): Date? = this[name].asDateOrNull()

fun Map<String, Any?>.timestamp(name: String): Timestamp = this[name]!!.asTimestamp()
fun Map<String, Any?>.timestampOrNull(name: String): Timestamp? = this[name].asTimestampOrNull()

fun Map<String, Any?>.instant(name: String): Instant = this[name]!!.asInstant()
fun Map<String, Any?>.instantOrNull(name: String): Instant? = this[name].asInstantOrNull()

fun Map<String, Any?>.localDate(name: String): LocalDate = this[name]!!.asLocalDate()
fun Map<String, Any?>.localDateOrNull(name: String): LocalDate? = this[name].asLocalDateOrNull()

fun Map<String, Any?>.localTime(name: String): LocalTime = this[name]!!.asLocalTime()
fun Map<String, Any?>.localTimeOrNull(name: String): LocalTime? = this[name].asLocalTimeOrNull()

fun Map<String, Any?>.localDateTime(name: String): LocalDateTime = this[name]!!.asLocalDateTime()
fun Map<String, Any?>.localDateTimeOrNull(name: String): LocalDateTime? = this[name].asLocalDateTimeOrNull()

fun Map<String, Any?>.offsetDateTime(name: String): OffsetDateTime = this[name]!!.asOffsetDateTime()
fun Map<String, Any?>.offsetDateTimeOrNull(name: String): OffsetDateTime? = this[name].asOffsetDateTimeOrNull()

fun Map<String, Any?>.uuid(name: String): UUID = this[name]!!.asUUID()
fun Map<String, Any?>.uuidOrNull(name: String): UUID? = this[name].asUUIDOrNull()
