package io.bluetape4k.geohash

import io.bluetape4k.geohash.GeoHash.Companion.BASE32_BITS
import io.bluetape4k.geohash.GeoHash.Companion.MAX_BIT_PRECISION
import io.bluetape4k.geohash.GeoHash.Companion.decodeArray
import io.bluetape4k.support.requireInRange
import io.bluetape4k.support.requireNotBlank


fun geoHashWithCharacters(point: WGS84Point, numberOfChars: Int): GeoHash {
    return geoHashWithCharacters(point.latitude, point.longitude, numberOfChars)
}

fun geoHashWithCharacters(latitude: Double, longitude: Double, numberOfChars: Int): GeoHash {
    numberOfChars.requireInRange(0, GeoHash.MAX_CHARACTER_PRECISION, "numberOfChars")
    val desiredPrecision = minOf(numberOfChars * 5, 60)
    return geoHashWithBits(latitude, longitude, desiredPrecision)
}

fun geoHashWithBits(point: WGS84Point, numberOfBits: Int): GeoHash {
    return geoHashWithBits(point.latitude, point.longitude, numberOfBits)
}

fun geoHashWithBits(latitude: Double, longitude: Double, numberOfBits: Int): GeoHash {
    numberOfBits.requireInRange(0, GeoHash.MAX_BIT_PRECISION, "numberOfBits")
    latitude.requireInRange(-90.0, 90.0, "latitude")
    longitude.requireInRange(-180.0, 180.0, "longitude")

    return GeoHash(latitude, longitude, numberOfBits)
}

fun geoHashOfBinaryString(binaryString: String): GeoHash {
    val geohash = GeoHash()
    for (i in binaryString.indices) {
        if (binaryString[i] == '1') {
            geohash.addOnBitToEnd()
        } else if (binaryString[i] == '0') {
            geohash.addOffBitToEnd()
        } else {
            throw IllegalArgumentException("$binaryString is not a valid geohash as a binary string")
        }
    }
    geohash.bits = geohash.bits shl (MAX_BIT_PRECISION - geohash.significantBits)
    val latitudeBits: LongArray = geohash.getRightAlignedLatitudeBits()
    val longitudeBits: LongArray = geohash.getRightAlignedLongitudeBits()
    return geohash.recombineLatLonBitsToHash(latitudeBits, longitudeBits)
}

fun geoHashOfString(geohashStr: String): GeoHash {
    geohashStr.requireNotBlank("geohashStr")

    val latitudeRange = doubleArrayOf(-90.0, 90.0)
    val longitudeRange = doubleArrayOf(-180.0, 180.0)
    var isEvenBit = true
    val hash = GeoHash()

    geohashStr.forEach { c ->
        var cd = 0

        require(!(c.code >= decodeArray.size || decodeArray[c.code].also { cd = it } < 0)) {
            "Invalid character character '$c' in geohash '$geohashStr'!"
        }

        for (j in 0 until BASE32_BITS) {
            val mask = GeoHash.BITS[j]
            if (isEvenBit) {
                hash.divideRangeDecode(longitudeRange, cd and mask != 0)
            } else {
                hash.divideRangeDecode(latitudeRange, cd and mask != 0)
            }
            isEvenBit = !isEvenBit
        }
    }
    val latitude = (latitudeRange[0] + latitudeRange[1]) / 2.0
    val longitude = (longitudeRange[0] + longitudeRange[1]) / 2.0
    hash.point = WGS84Point(latitude, longitude)
    hash.setBoundingBox(latitudeRange, longitudeRange)
    hash.bits = hash.bits shl MAX_BIT_PRECISION - hash.significantBits

    return hash
}

fun geoHashOfLongValue(hashVal: Long, significantBits: Int): GeoHash {
    val latitudeRange = doubleArrayOf(-90.0, 90.0)
    val longitudeRange = doubleArrayOf(-180.0, 180.0)
    var isEvenBit = true
    val hash = GeoHash()
    var binaryString = java.lang.Long.toBinaryString(hashVal)
    while (binaryString.length < MAX_BIT_PRECISION) {
        binaryString = "0$binaryString"
    }
    for (j in 0 until significantBits) {
        if (isEvenBit) {
            hash.divideRangeDecode(longitudeRange, binaryString[j] != '0')
        } else {
            hash.divideRangeDecode(latitudeRange, binaryString[j] != '0')
        }
        isEvenBit = !isEvenBit
    }
    val latitude = (latitudeRange[0] + latitudeRange[1]) / 2
    val longitude = (longitudeRange[0] + longitudeRange[1]) / 2
    hash.point = WGS84Point(latitude, longitude)
    hash.setBoundingBox(latitudeRange, longitudeRange)
    hash.bits = hash.bits shl MAX_BIT_PRECISION - hash.significantBits
    return hash
}

fun geoHashOfOrd(ord: Long, significantBits: Byte): GeoHash {
    val insignificantBits = MAX_BIT_PRECISION - significantBits
    return geoHashOfLongValue(ord shl insignificantBits, significantBits.toInt())
}


fun GeoHash.setBoundingBox(latitudeRange: DoubleArray, longitudeRange: DoubleArray) {
    this.boundingBox = BoundingBox(latitudeRange[0], latitudeRange[1], longitudeRange[0], longitudeRange[1])
}

internal fun GeoHash.divideRangeDecode(range: DoubleArray, b: Boolean): DoubleArray {
    val mid = (range[0] + range[1]) / 2.0
    if (b) {
        addOnBitToEnd()
        range[0] = mid
    } else {
        addOffBitToEnd()
        range[1] = mid
    }
    return range
}

fun GeoHash.stepsBetween(other: GeoHash): Long {
    require(this.significantBits == other.significantBits) {
        "It is only valid to compare the number of steps between two hashes if they have the same number of significant bits"
    }
    return other.ord() - this.ord()
}

/**
 * returns the 8 adjacent hashes for this one. They are in the following
 * order:<br></br>
 * N, NE, E, SE, S, SW, W, NW
 */
fun GeoHash.getAdjacent(): Array<GeoHash> {
    val northern: GeoHash = getNorthernNeighbor()
    val eastern: GeoHash = getEasternNeighbor()
    val southern: GeoHash = getSouthernNeighbor()
    val western: GeoHash = getWesternNeighbor()

    return arrayOf(
        northern,
        northern.getEasternNeighbor(),
        eastern,
        southern.getEasternNeighbor(),
        southern,
        southern.getWesternNeighbor(),
        western,
        northern.getWesternNeighbor()
    )
}
