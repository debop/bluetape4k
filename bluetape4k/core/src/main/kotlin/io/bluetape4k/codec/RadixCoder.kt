package io.bluetape4k.codec

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.dropItems
import io.bluetape4k.support.leadingZeros
import kotlin.math.ceil
import kotlin.math.ln

/**
 * 다양한 진법 (2 ~ 256) 으로 인코딩, 디코딩을 수행합니다.
 *
 * @param N        primitive array type used to represent numbers (byte or short)
 * @property base  진법 종류 (eg. 16, 32, 58, 62, 64 ...)
 */
abstract class RadixCoder<N>(
    protected val base: Int,
) {

    companion object: KLogging() {
        const val BASE_MIN: Int = 2
        const val BASE_MAX_U8: Int = 0x100
        const val BASE_MAX_U16: Int = 0x10000

        @JvmStatic
        fun u8(base: Int): RadixCoder<ByteArray> {
            return U8(base)
        }

        @JvmStatic
        fun u16(base: Int): RadixCoder<ShortArray> {
            return U16(base)
        }

        @JvmStatic
        internal fun ceilMultiply(n: Int, f: Double): Int = ceil(n * f).toInt()
    }


    protected val encodeFactor: Double
    protected val decodeFactor: Double

    init {
        require(base >= BASE_MIN) { "base[$base] must be >= $BASE_MIN" }

        val logBase = ln(base.toDouble())
        val logByte = ln(BASE_MAX_U8.toDouble())
        encodeFactor = logByte / logBase
        decodeFactor = logBase / logByte
    }

    /**
     * Encode bytes into a number
     *
     * @param bytes bytes to encode
     * @return a number encoded from bytes
     */
    abstract fun encode(bytes: ByteArray): N

    /**
     * Decode a number into bytes
     *
     * @param n a number
     * @return bytes decoded from n
     */
    abstract fun decode(n: N): ByteArray

    internal fun checkBaseMax(max: Int) {
        require(base <= max) { "base[$base] must be <= $max" }
    }

    internal fun checkDigitBase(digit: Int) {
        require(digit < base) { "digit[$digit] must be < base[$base]" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other != null && other is RadixCoder<*>) return base == other.base
        return false
    }

    override fun hashCode(): Int = base

    override fun toString(): String = javaClass.name + "($base)"

    class U8(base: Int): RadixCoder<ByteArray>(base) {

        init {
            checkBaseMax(BASE_MAX_U8)
        }

        override fun encode(bytes: ByteArray): ByteArray {
            val zeroCount = bytes.leadingZeros()

            if (zeroCount == bytes.size)
                return ByteArray(bytes.size)

            val capacity = zeroCount + ceilMultiply(bytes.size - zeroCount, encodeFactor)
            val dst = ByteArray(capacity)
            var j = capacity - 2
            for (i in zeroCount until bytes.size) {
                var carry = bytes[i].toInt() and 0xFF
                for (k in capacity - 1 downTo j + 1) {
                    carry += (dst[k].toInt() and 0xFF) shl Byte.SIZE_BITS
                    dst[k] = (carry % base).toByte()
                    carry /= base
                }
                while (carry > 0) {
                    dst[j--] = (carry % base).toByte()
                    carry /= base
                }
            }
            return dst.dropItems(j - zeroCount + 1)
        }

        override fun decode(n: ByteArray): ByteArray {
            val zeroCount = n.leadingZeros()

            if (zeroCount == n.size) {
                return ByteArray(n.size)
            }

            val capability = zeroCount + ceilMultiply(n.size - zeroCount, decodeFactor)
            val dst = ByteArray(capability)
            var j = capability - 2
            for (i in zeroCount until n.size) {
                var carry = n[i].toInt() and 0xFF
                checkDigitBase(carry)
                for (k in capability - 1 downTo j + 1) {
                    carry += (dst[k].toInt() and 0xFF) * base
                    dst[k] = carry.toByte()
                    carry = carry ushr Byte.SIZE_BITS
                }
                while (carry > 0) {
                    dst[j--] = carry.toByte()
                    carry = carry ushr Byte.SIZE_BITS
                }
            }
            return dst.dropItems(j - zeroCount + 1)
        }
    }

    class U16(base: Int): RadixCoder<ShortArray>(base) {
        init {
            checkBaseMax(BASE_MAX_U16)
        }

        override fun encode(bytes: ByteArray): ShortArray {
            val zeroCount = bytes.leadingZeros()

            if (zeroCount == bytes.size) {
                return ShortArray(bytes.size)
            }

            val capacity = zeroCount + ceilMultiply(bytes.size - zeroCount, encodeFactor)
            val dst = ShortArray(capacity)
            var j = capacity - 2
            for (i in zeroCount until bytes.size) {
                var carry = bytes[i].toInt() and 0xFF
                for (k in capacity - 1 downTo j + 1) {
                    carry += (dst[k].toInt() and 0xFFFF) shl Byte.SIZE_BITS
                    dst[k] = (carry % base).toShort()
                    carry /= base
                }
                while (carry > 0) {
                    dst[j--] = (carry % base).toShort()
                    carry /= base
                }
            }
            return dst.drop(j - zeroCount + 1).toShortArray()
        }

        override fun decode(n: ShortArray): ByteArray {
            val zeroCount = n.leadingZeros()

            if (zeroCount == n.size) {
                return ByteArray(n.size)
            }

            val capability = zeroCount + ceilMultiply(n.size - zeroCount, decodeFactor)
            val dst = ByteArray(capability)
            var j = capability - 2
            for (i in zeroCount until n.size) {
                var carry = n[i].toInt() and 0xFFFF
                checkDigitBase(carry)
                for (k in capability - 1 downTo j + 1) {
                    carry += (dst[k].toInt() and 0xFF) * base
                    dst[k] = carry.toByte()
                    carry = carry ushr Byte.SIZE_BITS
                }
                while (carry > 0) {
                    dst[j--] = carry.toByte()
                    carry = carry ushr Byte.SIZE_BITS
                }
            }
            return dst.dropItems(j - zeroCount + 1)
        }
    }
}
