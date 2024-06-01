package io.bluetape4k.geohash

import io.bluetape4k.geohash.utils.padLeft
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.hashOf
import java.io.Serializable
import kotlin.math.ceil

class GeoHash internal constructor(): Comparable<GeoHash>, Serializable {

    companion object: KLogging() {
        const val MAX_BIT_PRECISION = 64
        const val MAX_CHARACTER_PRECISION = 12

        internal const val serialVersionUID = -8553214249630252175L
        internal val BITS = intArrayOf(16, 8, 4, 2, 1)
        internal const val BASE32_BITS = 5

        val FIRST_BIT_FLAGGED = (0x8000000000000000UL).toLong()

        private val base32 = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        )

        internal val decodeArray: IntArray by lazy {
            val array = IntArray('z'.code + 1) { -1 }
            base32.forEachIndexed { index, c ->
                array[c.code] = index
            }
            array
        }

        @JvmStatic
        operator fun invoke(latitude: Double, longitude: Double, desiredPrecision: Int): GeoHash {
            return GeoHash().apply {
                this.point = WGS84Point(latitude, longitude)
                val desiredPrec = minOf(desiredPrecision, MAX_BIT_PRECISION)

                var isEvenBit = true
                val latitudeRange = doubleArrayOf(-90.0, 90.0)
                val longitudeRange = doubleArrayOf(-180.0, 180.0)

                while (significantBits < desiredPrec) {
                    if (isEvenBit) {
                        divideRangeEncode(longitude, longitudeRange)
                    } else {
                        divideRangeEncode(latitude, latitudeRange)
                    }
                    isEvenBit = !isEvenBit
                }

                this.setBoundingBox(latitudeRange, longitudeRange)
                bits = bits shl (MAX_BIT_PRECISION - desiredPrec)
            }
        }
    }

    var bits: Long = 0L
        internal set
    var point: WGS84Point = WGS84Point(0.0, 0.0)
        internal set
    var boundingBox: BoundingBox = BoundingBox(0.0, 0.0, 0.0, 0.0)
        internal set
    var significantBits: Byte = 0
        internal set

    fun significantBits(): Int = significantBits.toInt()

    val longValue: Long get() = bits

    fun next(step: Int = 1): GeoHash {
        return geoHashOfOrd(ord() + step, significantBits)
    }

    fun prev(): GeoHash = next(-1)

    fun ord(): Long {
        val insignificantBits = MAX_BIT_PRECISION - significantBits
        return bits ushr insignificantBits
    }

    fun getCharacterPrecision(): Int {
        check(significantBits % 5 == 0) { "significant bits는 5의 배수이어야 합니다 [$significantBits]" }
        return significantBits / 5
    }


    fun toBase32(): String {
        check(significantBits % 5 == 0) {
            "Cannot convert a geohash to base32 if the precision is not a multiple of 5. significantBits=[$significantBits]"
        }

        return buildString {
            val firstFiveBitsMask = 0xf800000000000000UL.toLong()
            var bitsCopy = bits
            val partialChunks = ceil(significantBits.toDouble() / 5).toInt()

            repeat(partialChunks) {
                val pointer = ((bitsCopy and firstFiveBitsMask) ushr 59).toInt()
                append(base32[pointer])
                bitsCopy = bitsCopy shl 5
            }
        }
    }

    /**
     * returns true iff this is within the given geohash bounding box.
     */
    fun within(boundingBox: GeoHash): Boolean {
        return bits and boundingBox.mask() == boundingBox.bits
    }

    /**
     * find out if the given point lies within this hashes bounding box.<br></br>
     * *Note: this operation checks the bounding boxes coordinates, i.e. does
     * not use the [GeoHash]s special abilities.s*
     */
    operator fun contains(point: WGS84Point): Boolean {
        return boundingBox.contains(point)
    }

    /**
     * returns the [WGS84Point] that was originally used to set up this.<br></br>
     * If it was built from a base32-[String], this is the center point of
     * the bounding box.
     */
    val originatingPoint: WGS84Point get() = point

    /**
     * return the center of this [GeoHash]s bounding box. this is rarely
     * the same point that was used to build the hash.
     */
    // TODO: make sure this method works as intented for corner cases!
    val boundingBoxCenter: WGS84Point get() = boundingBox.getCenter()

    @Suppress("UNUSED_PARAMETER")
    fun enclosesCircleAroundPoint(point: WGS84Point?, radius: Double): Boolean {
        return false
    }

    internal fun recombineLatLonBitsToHash(latBits: LongArray, lonBits: LongArray): GeoHash {
        val hash = GeoHash()
        var isEvenBit = false
        latBits[0] = latBits[0] shl (MAX_BIT_PRECISION - latBits[1]).toInt()
        lonBits[0] = lonBits[0] shl (MAX_BIT_PRECISION - lonBits[1]).toInt()

        val latitudeRange = doubleArrayOf(-90.0, 90.0)
        val longitudeRange = doubleArrayOf(-180.0, 180.0)

        for (i in 0 until (latBits[1] + lonBits[1])) {
            if (isEvenBit) {
                hash.divideRangeDecode(latitudeRange, (latBits[0] and FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED)
                latBits[0] = latBits[0] shl 1
            } else {
                hash.divideRangeDecode(longitudeRange, (lonBits[0] and FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED)
                lonBits[0] = lonBits[0] shl 1
            }
            isEvenBit = !isEvenBit
        }
        hash.bits = hash.bits shl (MAX_BIT_PRECISION - hash.significantBits)
        hash.setBoundingBox(latitudeRange, longitudeRange)
        hash.point = hash.boundingBoxCenter

        return hash
    }

    fun getNorthernNeighbor(): GeoHash {
        val latitudeBits = getRightAlignedLatitudeBits()
        val longitudeBits = getRightAlignedLongitudeBits()
        latitudeBits[0] = maskLastNBits(latitudeBits[0] + 1L, latitudeBits[1])
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits)
    }

    fun getSouthernNeighbor(): GeoHash {
        val latitudeBits = getRightAlignedLatitudeBits()
        val longitudeBits = getRightAlignedLongitudeBits()
        latitudeBits[0] = maskLastNBits(latitudeBits[0] - 1L, latitudeBits[1])
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits)
    }

    fun getEasternNeighbor(): GeoHash {
        val latitudeBits = getRightAlignedLatitudeBits()
        val longitudeBits = getRightAlignedLongitudeBits()
        longitudeBits[0] = maskLastNBits(longitudeBits[0] + 1L, longitudeBits[1])
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits)
    }

    fun getWesternNeighbor(): GeoHash {
        val latitudeBits = getRightAlignedLatitudeBits()
        val longitudeBits = getRightAlignedLongitudeBits()
        longitudeBits[0] = maskLastNBits(longitudeBits[0] - 1L, longitudeBits[1])
        return recombineLatLonBitsToHash(latitudeBits, longitudeBits)
    }

    internal fun getRightAlignedLatitudeBits(): LongArray {
        val copyOfBits = bits shl 1
        val second = getNumberOfLatLonBits()[0]
        val value = extractEverySecondBit(copyOfBits, second)
        return longArrayOf(value, second.toLong())
    }

    internal fun getRightAlignedLongitudeBits(): LongArray {
        val copyOfBits = bits
        val value = extractEverySecondBit(copyOfBits, getNumberOfLatLonBits()[1])
        return longArrayOf(value, getNumberOfLatLonBits()[1].toLong())
    }

    private fun extractEverySecondBit(copyOfBits: Long, numberOfBits: Int): Long {
        var bits = copyOfBits
        var value = 0L

        for (i in 0 until numberOfBits) {
            if ((bits and FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED) {
                value = value or 0x1L
            }
            value = value shl 1
            bits = bits shl 2
        }
        value = value ushr 1
        return value
    }

    private fun getNumberOfLatLonBits(): IntArray {
        return if (significantBits % 2 == 0) {
            intArrayOf(significantBits / 2, significantBits / 2)
        } else {
            intArrayOf(significantBits / 2, significantBits / 2 + 1)
        }
    }

    internal fun addOnBitToEnd() {
        significantBits++
        bits = bits shl 1
        bits = bits or 0x1L
    }

    internal fun addOffBitToEnd() {
        significantBits++
        bits = bits shl 1
    }

    private fun divideRangeEncode(value: Double, range: DoubleArray) {
        val mid = (range[0] + range[1]) / 2
        if (value >= mid) {
            addOnBitToEnd()
            range[0] = mid
        } else {
            addOffBitToEnd()
            range[1] = mid
        }
    }

    fun toBinaryString(): String = buildString {
        var bitsCopy = bits
        for (i in 0 until significantBits) {
            if ((bitsCopy and FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED) {
                append('1')
            } else {
                append('0')
            }
            bitsCopy = bitsCopy shl 1
        }
    }

    /**
     * return a long mask for this hashes significant bits.
     */
    private fun mask(): Long {
        return when (significantBits.toInt()) {
            0    -> 0
            else -> FIRST_BIT_FLAGGED shr (significantBits - 1)
        }
    }

    private fun maskLastNBits(value: Long, n: Long): Long {
        var mask = -0x1L
        mask = mask ushr (MAX_BIT_PRECISION - n).toInt()
        return value and mask
    }

    override fun compareTo(other: GeoHash): Int {
        val bitsCmp: Int = (bits xor FIRST_BIT_FLAGGED).compareTo(other.bits xor FIRST_BIT_FLAGGED)
        return if (bitsCmp != 0) {
            bitsCmp
        } else {
            significantBits.toInt().compareTo(other.significantBits.toInt())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true

        return other is GeoHash &&
                bits == other.bits &&
                significantBits == other.significantBits
    }

    override fun hashCode(): Int {
        return hashOf(bits xor (bits ushr 32), significantBits)
    }

    override fun toString(): String = when (significantBits % 5) {
        0    -> "${padLeft(java.lang.Long.toBinaryString(bits), 64, "0")} -> $boundingBox -> ${toBase32()}"
        else -> "${padLeft(java.lang.Long.toBinaryString(bits), 64, "0")} -> $boundingBox, bits: $significantBits"
    }
}
