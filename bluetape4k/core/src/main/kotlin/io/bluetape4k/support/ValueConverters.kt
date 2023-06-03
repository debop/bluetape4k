package io.bluetape4k.support

import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong


fun Any?.asDouble(defaultValue: Double = 0.0): Double = asDoubleOrNull() ?: defaultValue
fun Any?.asDoubleOrNull(): Double? = runCatching {
    when (this) {
        null            -> null
        is Double       -> this
        is Number       -> this.toDouble()
        is CharSequence -> this.toString().toDouble()
        else            -> this.toString().parseNumber()
    }
}.getOrNull()

/**
 * 객체를 Float 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asFloat(defaultValue: Float = 0.0F): Float = asFloatOrNull() ?: defaultValue
fun Any?.asFloatOrNull(): Float? = runCatching {
    when (this) {
        null            -> null
        is Float        -> this
        is Number       -> this.toFloat()
        is CharSequence -> this.toString().parseNumber()
        else            -> this.asDoubleOrNull()?.toFloat()
    }
}.getOrNull()

/**
 * 객체를 Long 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asLong(defaultValue: Long = 0L): Long = asLongOrNull() ?: defaultValue
fun Any?.asLongOrNull(): Long? = runCatching {
    when (this) {
        null            -> null
        is Long         -> this
        is Number       -> this.toLong()
        is CharSequence -> this.toString().toLong()
        else            -> this.asBigDecimalOrNull()?.toLong()
    }
}.getOrNull()

/**
 * 객체를 Int 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asInt(defaultValue: Int = 0): Int = asIntOrNull() ?: defaultValue
fun Any?.asIntOrNull(): Int? = runCatching {
    when (this) {
        null      -> null
        is Int    -> this
        is Number -> this.toInt()
        else      -> this.asLongOrNull()?.toInt()
    }
}.getOrNull()

/**
 * 객체를 Short 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asShort(defaultValue: Short = 0): Short = asShortOrNull() ?: defaultValue
fun Any?.asShortOrNull(): Short? = runCatching {
    when (this) {
        null      -> null
        is Short  -> this
        is Number -> this.toShort()
        else      -> this.asLongOrNull()?.toShort()
    }
}.getOrNull()

/**
 * 객체를 Byte 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asByte(defaultValue: Byte = 0.toByte()): Byte = asByteOrNull() ?: defaultValue
fun Any?.asByteOrNull(): Byte? = runCatching {
    when (this) {
        null      -> null
        is Char   -> this.code.toByte()
        is Byte   -> this
        is Number -> this.toByte()
        else      -> this.asLongOrNull()?.toByte()
    }
}.getOrNull()

/**
 * 객체를 Char 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asChar(defaultValue: Char = 0.toChar()): Char = asCharOrNull() ?: defaultValue
fun Any?.asCharOrNull(): Char? = runCatching {
    when (this) {
        null            -> null
        is Char         -> this
        is Byte         -> this.toInt().toChar()
        is CharSequence -> if (this.length == 1) first() else asIntOrNull()?.toChar()
        else            -> asIntOrNull()?.toChar()
    }
}.getOrNull()

fun Any?.asBoolean(defaultValue: Boolean = false): Boolean = asBooleanOrNull() ?: defaultValue
fun Any?.asBooleanOrNull(): Boolean? = runCatching {
    when (this) {
        null       -> null
        is Boolean -> this
        is Number  -> this.toInt() != 0
        is Char    -> this == 'y' || this == 'Y'
        else       -> this.toString().toBoolean()
    }
}.getOrNull()

/**
 * 객체를 BigDecimal 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asBigDecimal(defaultValue: BigDecimal = BigDecimal.ZERO): BigDecimal = asBigDecimalOrNull() ?: defaultValue
fun Any?.asBigDecimalOrNull(): BigDecimal? = runCatching {
    when (this) {
        null          -> null
        is BigDecimal -> this
        is Number     -> BigDecimal.valueOf(this.toDouble())
        else          -> BigDecimal(toString())
    }
}.getOrNull()

/**
 * 객체를 BigInteger 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asBigInt(defaultValue: BigInteger = BigInteger.ZERO): BigInteger = asBigIntOrNull() ?: defaultValue
fun Any?.asBigIntOrNull(): BigInteger? = runCatching {
    when (this) {
        null          -> null
        is BigInteger -> this
        is Number     -> BigInteger.valueOf(this.toLong())
        else          -> BigInteger(toString())
    }
}.getOrNull()

/**
 * 객체를 String 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asString(defaultValue: String = ""): String = asStringOrNull() ?: defaultValue
fun Any?.asStringOrNull(): String? = this?.toString()

internal val SIMPLE_DATE_FORMAT = SimpleDateFormat()

/**
 * 객체를 Date 수형으로 변환합니다.
 *
 * @receiver Any?
 * @param defaultValue 변환 실패 시 대체 값
 */
fun Any?.asDate(defaultValue: Date = Date(0L)): Date = asDateOrNull() ?: defaultValue
fun Any?.asDateOrNull(): Date? = runCatching {
    when (this) {
        null                -> null
        is Number           -> Date(this.toLong())
        is Date             -> this
        is Instant          -> Date.from(this)
        is TemporalAccessor -> Date.from(Instant.from(this))
        else                -> SIMPLE_DATE_FORMAT.parse(asString())
    }
}.getOrNull()

fun Any?.asTimestamp(defaultValue: Timestamp = Timestamp(0L)): Timestamp = asTimestampOrNull() ?: defaultValue
fun Any?.asTimestampOrNull(): Timestamp? = runCatching {
    when (this) {
        null                -> null
        is Number           -> Timestamp(this.toLong())
        is Timestamp        -> this
        is Instant          -> Timestamp.from(this)
        is TemporalAccessor -> Timestamp.from(Instant.from(this))
        else                -> Timestamp.valueOf(this.asString())
    }
}.getOrNull()

fun Any?.asInstant(defaultValue: Instant = Instant.ofEpochMilli(0L)): Instant = asInstantOrNull() ?: defaultValue
fun Any?.asInstantOrNull(): Instant? = runCatching {
    when (this) {
        null                -> null
        is Number           -> Instant.ofEpochMilli(this.toLong())
        is Instant          -> this
        is TemporalAccessor -> Instant.from(this)
        else                -> Instant.parse(this.asString())
    }
}.getOrNull()

fun Any?.asLocalDate(defaultValue: LocalDate = LocalDate.MIN): LocalDate = asLocalDateOrNull() ?: defaultValue
fun Any?.asLocalDateOrNull(): LocalDate? = runCatching {
    when (this) {
        null                -> null
        is LocalDate        -> this
        is Instant          -> LocalDate.ofInstant(this, ZoneId.systemDefault())
        is TemporalAccessor -> LocalDate.from(this)
        else                -> LocalDate.parse(this.toString())
    }
}.getOrNull()

fun Any?.asLocalTime(defaultValue: LocalTime = LocalTime.MIN): LocalTime = asLocalTimeOrNull() ?: defaultValue
fun Any?.asLocalTimeOrNull(): LocalTime? = runCatching {
    when (this) {
        null                -> null
        is LocalTime        -> this
        is Instant          -> LocalTime.ofInstant(this, ZoneId.systemDefault())
        is TemporalAccessor -> LocalTime.from(this)
        else                -> LocalTime.parse(this.toString())
    }
}.getOrNull()

fun Any?.asLocalDateTime(defaultValue: LocalDateTime = LocalDateTime.MIN): LocalDateTime =
    asLocalDateTimeOrNull() ?: defaultValue

fun Any?.asLocalDateTimeOrNull(): LocalDateTime? = runCatching {
    when (this) {
        null                -> null
        is LocalDateTime    -> this
        is Instant          -> LocalDateTime.ofInstant(this, ZoneId.systemDefault())
        is TemporalAccessor -> LocalDateTime.from(this)
        else                -> LocalDateTime.parse(this.toString())
    }
}.getOrNull()

fun Any?.asOffsetDateTime(defaultValue: OffsetDateTime = OffsetDateTime.MIN): OffsetDateTime =
    asOffsetDateTimeOrNull() ?: defaultValue

fun Any?.asOffsetDateTimeOrNull(): OffsetDateTime? = runCatching {
    when (this) {
        null                -> null
        is OffsetDateTime   -> this
        is TemporalAccessor -> OffsetDateTime.from(this)
        else                -> OffsetDateTime.parse(this.toString())
    }
}.getOrNull()

fun Any?.asUUID(defaultValue: UUID): UUID = asUUIDOrNull() ?: defaultValue
fun Any?.asUUIDOrNull(): UUID? = runCatching {
    when (this) {
        null    -> null
        is UUID -> this
        else    -> UUID.fromString(this.toString())
    }
}.getOrNull()

fun Any?.asByteArray(charset: Charset = Charsets.UTF_8, defaultValue: ByteArray = emptyByteArray): ByteArray =
    asByteArrayOrNull(charset) ?: defaultValue

fun Any?.asByteArrayOrNull(charset: Charset = Charsets.UTF_8): ByteArray? = runCatching {
    when (this) {
        null         -> null
        is ByteArray -> this
        else         -> toString().toByteArray(charset)
    }
}.getOrNull()

//
// Floor, Round for specific decimal point
//

//private val decimalFormats = ConcurrentHashMap<Int, DecimalFormat>()
//
//private fun getDecimalFormat(decimalCount: Int): DecimalFormat =
//    decimalFormats.computeIfAbsent(decimalCount) { dc ->
//        if (dc > 0) DecimalFormat("." + "#".repeat(dc))
//        else DecimalFormat("#")
//    }

/**
 * 객체를 [Float]로 변환하면서 [decimalCount] 자릿수에서 내림을 수행합니다.
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asFloatFloor(decimalCount: Int = 0, defaultValue: Float = 0.0F): Float =
    asFloatFloorOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asFloatFloorOrNull(decimalCount: Int = 0): Float? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toFloat()
        }
        val decimal = 10.0.pow(decimalCount).toFloat()
        floor(asFloat() * decimal) / decimal
    }.getOrNull()
}

/**
 * 객체를 [Float]로 변환하면서 [decimalCount] 자릿수에서 반올림을 수행합니다.
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asFloatRound(decimalCount: Int = 0, defaultValue: Float = 0.0F): Float =
    asFloatRoundOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asFloatRoundOrNull(decimalCount: Int = 0): Float? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toFloat()
        }
        val decimal = 10.0.pow(decimalCount).toFloat()
        (this.asFloat() * decimal).roundToLong() / decimal
    }.getOrNull()
}

/**
 * 객체를 [Float]로 변환하면서 [decimalCount] 자릿수에서 올림을 수행합니다.
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asFloatCeil(decimalCount: Int = 0, defaultValue: Float = 0.0F): Float =
    asFloatCeilOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asFloatCeilOrNull(decimalCount: Int = 0): Float? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toFloat()
        }

        val decimal = 10.0.pow(decimalCount).toFloat()
        ceil(asFloat() * decimal) / decimal
    }.getOrNull()
}

/**
 * 객체를 [Double]로 변환하면서 [decimalCount] 자릿수에서 내림을 수행합니다.
 *
 * ```kotlin
 * 1.00123456.asDoubleFloor(2) shouldBeEqualTo 1.00
 * 1.00123456.asDoubleFloor(1) shouldBeEqualTo 1.0
 * "13567.6".asDoubleFloor(-2) shouldBeEqualTo 13500.0
 * ```
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asDoubleFloor(decimalCount: Int = 0, defaultValue: Double = 0.0): Double =
    asDoubleFloorOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asDoubleFloorOrNull(decimalCount: Int = 0): Double? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toDouble()
        }

        val decimal = 10.0.pow(decimalCount)
        floor(asDouble() * decimal) / decimal
    }.getOrNull()
}

/**
 * 객체를 [Double]로 변환하면서 [decimalCount] 자릿수에서 반올림을 수행합니다.
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asDoubleRound(decimalCount: Int = 0, defaultValue: Double = 0.0): Double =
    asDoubleRoundOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asDoubleRoundOrNull(decimalCount: Int = 0): Double? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toDouble()
        }
        val decimal = 10.0.pow(decimalCount)
        (asDouble() * decimal).roundToLong() / decimal
    }.getOrNull()
}

/**
 * 객체를 [Double]로 변환하면서 [decimalCount] 자릿수에서 올림을 수행합니다.
 *
 * ```kotlin
 * 1.00123456.asDoubleFloor(2) shouldBeEqualTo 1.00
 * 1.00123456.asDoubleFloor(1) shouldBeEqualTo 1.0
 * "13567.6".asDoubleFloor(-2) shouldBeEqualTo 13600.0
 * ```
 *
 * @param decimalCount 자릿 수
 */
@JvmOverloads
fun Any?.asDoubleCeil(decimalCount: Int = 0, defaultValue: Double = 0.0): Double =
    asDoubleCeilOrNull(decimalCount) ?: defaultValue

@JvmOverloads
fun Any?.asDoubleCeilOrNull(decimalCount: Int = 0): Double? = this?.run {
    runCatching {
        if (decimalCount == 0) {
            return this.asLong().toDouble()
        }
        val decimal = 10.0.pow(decimalCount)
        ceil(asDouble() * decimal) / decimal
    }.getOrNull()
}
