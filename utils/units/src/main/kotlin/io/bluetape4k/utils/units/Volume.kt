package io.bluetape4k.utils.units

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.units.VolumeUnit.CC
import io.bluetape4k.utils.units.VolumeUnit.CENTIMETER_3
import io.bluetape4k.utils.units.VolumeUnit.DECILITER
import io.bluetape4k.utils.units.VolumeUnit.LITER
import io.bluetape4k.utils.units.VolumeUnit.METER_3
import io.bluetape4k.utils.units.VolumeUnit.MILLILETER
import java.io.Serializable
import kotlin.math.absoluteValue

fun volumeOf(volumn: Number = 0.0, unit: VolumeUnit = LITER): Volume = Volume(volumn, unit)

fun <T: Number> T.volumeBy(unit: VolumeUnit): Volume = Volume(this, unit)

fun <T: Number> T.cc(): Volume = volumeBy(CC)
fun <T: Number> T.milliliter(): Volume = volumeBy(MILLILETER)
fun <T: Number> T.deciliter(): Volume = volumeBy(DECILITER)
fun <T: Number> T.liter(): Volume = volumeBy(LITER)
fun <T: Number> T.centimeter3(): Volume = volumeBy(CENTIMETER_3)
fun <T: Number> T.meter3(): Volume = volumeBy(METER_3)

operator fun <T: Number> T.times(volume: Volume): Volume = volume * this

/**
 * 체적 (Volume) 종류 및 단위
 */
enum class VolumeUnit(val unitName: String, val factor: Double) {

    CC("cc", 1.0e-9),
    CENTIMETER_3("cm^3", 1.0e-3),
    MILLILETER("ml", 1.0e-3),
    DECILITER("dl", 1.0e-2),
    LITER("l", 1.0),
    METER_3("m^3", 1.0e3);

    // 영국 부피 단위는 따로 클래스를 만들 예정입니다.
    //    GALLON("gl", 1.0 / 0.264172),
    //    BARREL("barrel", 1.0 / 0.006293),
    //    FLUID_OUNCE("oz", 1.0 / 33.814022);

    companion object {

        @JvmField
        val VALS = values()

        @JvmStatic
        fun parse(unitStr: String): VolumeUnit {
            var lower = unitStr.lowercase()
            if (lower.endsWith("s"))
                lower = lower.dropLast(1)

            return VALS.find { it.unitName == lower }
                ?: throw NumberFormatException("Unknown Volume unit. unitStr=$unitStr")
        }
    }
}

/**
 * 체적 (Volume) 을 나타내는 클래스.
 * 리터를 기본단위로 사용합니다.
 *
 * @property value CC 단위의 값
 */
@JvmInline
value class Volume(val value: Double = 0.0): Comparable<Volume>, Serializable {

    operator fun plus(other: Volume): Volume = Volume(value + other.value)
    operator fun minus(other: Volume): Volume = Volume(value - other.value)

    operator fun times(scalar: Number): Volume = Volume(value * scalar.toDouble())
    operator fun div(scalar: Number): Volume = Volume(value / scalar.toDouble())

    operator fun div(area: Area): Length = Length(inCC() / area.value)
    operator fun div(length: Length): Area = Area(inCC() / length.value)
    operator fun unaryMinus(): Volume = Volume(-value)

    fun inCC() = value / CC.factor
    fun inMilliLiter() = value / MILLILETER.factor
    fun inDeciLiter() = value / DECILITER.factor
    fun inLiter() = value / LITER.factor
    fun inCentiMeter3() = value / CENTIMETER_3.factor
    fun inMeter3() = value / METER_3.factor

    override fun compareTo(other: Volume): Int = value.compareTo(other.value)

    override fun toString(): String = toHuman(CC)

    fun toHuman(): String {
        val absValue = value.absoluteValue
        val displayUnit = VolumeUnit.VALS.lastOrNull { absValue / it.factor > 1.0 } ?: CC

        return "%.1f %s".format(value / displayUnit.factor, displayUnit.unitName)
    }

    fun toHuman(unit: VolumeUnit = LITER): String =
        "%.1f %s".format(value / unit.factor, unit.unitName)

    companion object: KLogging() {

        @JvmStatic
        val Zero by lazy { Volume(0.0) }

        @JvmStatic
        val MaxValue by lazy { Volume(Double.MAX_VALUE) }

        @JvmStatic
        val MinValue by lazy { Volume(Double.MIN_VALUE) }

        @JvmStatic
        val PositiveInf by lazy { Volume(Double.POSITIVE_INFINITY) }

        @JvmStatic
        val NegativeInf by lazy { Volume(Double.NEGATIVE_INFINITY) }

        @JvmStatic
        val NaN by lazy { Volume(Double.NaN) }

        operator fun invoke(volumn: Number, unit: VolumeUnit = LITER): Volume =
            Volume(volumn.toDouble() * unit.factor)

        fun parse(expr: String?): Volume {
            if (expr.isNullOrBlank())
                return NaN

            try {
                val (vol, unit) = expr.trim().split(" ", limit = 2)
                return Volume(vol.toDouble(), VolumeUnit.parse(unit))
            } catch (e: Exception) {
                throw NumberFormatException("Unknown Volume string. expr=$expr")
            }
        }
    }
}
