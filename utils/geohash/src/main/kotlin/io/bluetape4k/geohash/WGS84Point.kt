package io.bluetape4k.geohash

import io.bluetape4k.geohash.utils.VincentyGeodesy
import io.bluetape4k.support.requireInRange
import java.io.Serializable

data class WGS84Point(
    val latitude: Double,
    val longitude: Double,
): Serializable {
    init {
        latitude.requireInRange(-90.0, 90.0, "latitude")
        longitude.requireInRange(-180.0, 180.0, "longitude")
    }

    fun toPair(): Pair<Double, Double> = latitude to longitude
}

fun wgs84PointOf(latitude: Double, longitude: Double): WGS84Point {
    latitude.requireInRange(-90.0, 90.0, "latitude")
    longitude.requireInRange(-180.0, 180.0, "longitude")
    return WGS84Point(latitude, longitude)
}

fun WGS84Point.moveInDirection(
    bearingInDegrees: Double,
    distanceInMeters: Double,
): WGS84Point {
    return VincentyGeodesy.moveInDirection(this, bearingInDegrees, distanceInMeters)
}

fun WGS84Point.distanceInMeters(other: WGS84Point): Double {
    return VincentyGeodesy.distanceInMeters(this, other)
}
